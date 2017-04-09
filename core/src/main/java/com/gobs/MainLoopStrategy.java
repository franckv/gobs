package com.gobs;

import com.artemis.BaseSystem;
import com.artemis.SystemInvocationStrategy;
import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;
import com.badlogic.gdx.utils.ObjectMap;
import com.gobs.systems.RenderingSystem;

public class MainLoopStrategy extends SystemInvocationStrategy {
    protected Bag<BaseSystem> logicalSystems;
    protected Bag<BaseSystem> renderingSystems;

    protected final BitVector logicalDisabled = new BitVector();
    protected final BitVector renderingDisabled = new BitVector();

    private double accumulator = 0.0;
    private float logicalStep;

    private boolean perfMonitoring;
    private ObjectMap<String, Long> perfData = new ObjectMap<>();
    private long currentTime;
    private long frame;

    public MainLoopStrategy(float logicalStep, boolean perfMonitoring) {
        logicalSystems = new Bag<>(BaseSystem.class);
        renderingSystems = new Bag<>(BaseSystem.class);

        this.logicalStep = logicalStep;

        this.perfMonitoring = perfMonitoring;
        currentTime = 0;
        frame = 0;
    }

    @Override
    protected void initialize() {
        BaseSystem[] systemsData = systems.getData();
        for (int i = 0, s = systems.size(); s > i; i++) {
            if (isRendering(systemsData[i])) {
                renderingSystems.add(systemsData[i]);
            } else {
                logicalSystems.add(systemsData[i]);
            }
        }
    }

    @Override
    protected void process() {
        accumulator += world.getDelta();

        while (accumulator >= logicalStep) {
            accumulator -= logicalStep;
            world.setDelta(logicalStep);
            processLogical();
        }

        processRendering();

        updateEntityStates();

        ++frame;
    }

    @Override
    public void setEnabled(BaseSystem system, boolean value) {
        Class<? extends BaseSystem> target = system.getClass();

        if (isRendering(system)) {
            for (int i = 0; i < renderingSystems.size(); i++) {
                if (target == renderingSystems.get(i).getClass()) {
                    renderingDisabled.set(i, !value);
                }
            }
        } else {
            for (int i = 0; i < logicalSystems.size(); i++) {
                if (target == logicalSystems.get(i).getClass()) {
                    logicalDisabled.set(i, !value);
                }
            }
        }
    }

    @Override
    public boolean isEnabled(BaseSystem system) {
        Class<? extends BaseSystem> target = system.getClass();

        if (isRendering(system)) {
            for (int i = 0; i < renderingSystems.size(); i++) {
                if (target == renderingSystems.get(i).getClass()) {
                    return !renderingDisabled.get(i);
                }
            }
        } else {
            for (int i = 0; i < logicalSystems.size(); i++) {
                if (target == logicalSystems.get(i).getClass()) {
                    return !logicalDisabled.get(i);
                }
            }
        }

        throw new RuntimeException("System not found");
    }

    public ObjectMap<String, Long> getPerfData() {
        return perfData;
    }

    public boolean hasPerfData() {
        return perfMonitoring;
    }

    private void processLogical() {
        for (int i = 0; i < logicalSystems.size(); i++) {
            if (logicalDisabled.get(i)) {
                continue;
            }

            updateEntityStates();

            if (perfMonitoring) {
                currentTime = System.nanoTime();
            }

            logicalSystems.get(i).process();

            if (perfMonitoring && frame % 75 == 0) {
                long delta = System.nanoTime() - currentTime;
                String sys = logicalSystems.get(i).getClass().getSimpleName();
                perfData.put(sys, delta / 1000);
            }
        }
    }

    private void processRendering() {
        for (int i = 0; i < renderingSystems.size(); i++) {
            if (renderingDisabled.get(i)) {
                continue;
            }

            updateEntityStates();

            if (perfMonitoring) {
                currentTime = System.nanoTime();
            }

            renderingSystems.get(i).process();

            if (perfMonitoring && frame % 75 == 0) {
                long delta = System.nanoTime() - currentTime;
                String sys = renderingSystems.get(i).getClass().getSimpleName();
                perfData.put(sys, delta / 1000);
            }
        }
    }

    private boolean isRendering(BaseSystem system) {
        return system instanceof RenderingSystem;
    }
}
