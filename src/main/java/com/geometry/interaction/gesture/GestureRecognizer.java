package com.geometry.interaction.gesture;

import com.geometry.interaction.event.GestureEvent;
import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.Vec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 05 - Recognises gestures from a stream of PointerEvents.
 *
 * The GestureRecognizer maintains per-pointer state across events and emits
 * GestureEvents when a gesture pattern is detected.
 *
 * Supported gestures:
 *   - DRAG:   single pointer DOWN → MOVE* → UP
 *   - PINCH:  two pointers moving toward/away from each other
 *   - ROTATE: two pointers rotating around a common centre
 *   - TAP:    single pointer DOWN + UP with little movement
 *
 * The recogniser does NOT know about SceneObjects or Actions — it only
 * translates raw pointer events into semantic gestures.
 */
public class GestureRecognizer {

    /** Maximum allowed movement for a tap (in pixels). */
    private static final float TAP_MAX_MOVEMENT = 10f;

    /** State for two-finger pinch/rotate gestures. */
    private static class TwoFingerState {
        Vec2 finger1DownPos;
        Vec2 finger2DownPos;
        Vec2 finger1LastPos;
        Vec2 finger2LastPos;
        float initialDistance;
        float initialAngle;
        boolean active;
        int finger1Id = -1;
        int finger2Id = -1;

        TwoFingerState() {
            this.active = false;
        }
    }

    /** Per-pointer gesture state. */
    private static class PointerGestureState {
        Vec2 downPos;
        Vec2 lastPos;
        boolean active;
        float totalDistance;

        PointerGestureState() {
            this.active = false;
            this.totalDistance = 0f;
        }
    }

    private final PointerGestureState[] pointers;
    private final TwoFingerState twoFinger;
    private final List<GestureEvent> pendingEvents;

    public GestureRecognizer() {
        this.pointers = new PointerGestureState[10];
        for (int i = 0; i < pointers.length; i++) {
            pointers[i] = new PointerGestureState();
        }
        this.twoFinger = new TwoFingerState();
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Process a batch of pointer events and emit recognised gestures.
     *
     * @param events list of pointer events from an InputDevice
     * @return list of recognised GestureEvents (may be empty)
     */
    public List<GestureEvent> process(List<PointerEvent> events) {
        pendingEvents.clear();
        for (PointerEvent event : events) {
            processPointerEvent(event);
        }
        List<GestureEvent> result = new ArrayList<>(pendingEvents);
        pendingEvents.clear();
        return result;
    }

    private void processPointerEvent(PointerEvent event) {
        int pid = event.getPointerId();
        if (pid < 0 || pid >= pointers.length) {
            return;
        }

        PointerGestureState state = pointers[pid];

        switch (event.getEventType()) {
            case DOWN:
                onPointerDown(pid, event);
                break;
            case MOVE:
                onPointerMove(pid, event);
                break;
            case UP:
                onPointerUp(pid, event);
                break;
            case CLICK:
                // Click is handled as a potential tap
                break;
        }
    }

    private void onPointerDown(int pid, PointerEvent event) {
        Vec2 pos = event.getPosition();
        pointers[pid].downPos = pos;
        pointers[pid].lastPos = pos;
        pointers[pid].active = true;
        pointers[pid].totalDistance = 0f;

        // Check if this completes a two-finger gesture start
        checkTwoFingerStart(pid);
    }

    private void onPointerMove(int pid, PointerEvent event) {
        if (!pointers[pid].active) {
            return;
        }
        Vec2 newPos = event.getPosition();
        Vec2 delta = event.getDelta();
        pointers[pid].totalDistance += delta.length();
        pointers[pid].lastPos = newPos;

        // Check two-finger updates
        if (twoFinger.active) {
            updateTwoFinger(pid, newPos);
        }
    }

    private void onPointerUp(int pid, PointerEvent event) {
        if (!pointers[pid].active) {
            return;
        }
        Vec2 lastPos = pointers[pid].lastPos;
        pointers[pid].active = false;

        // Determine gesture type based on movement
        float moveDist = lastPos.subtract(pointers[pid].downPos).length();

        if (moveDist <= TAP_MAX_MOVEMENT) {
            pendingEvents.add(new GestureEvent(
                    GestureEvent.GestureType.TAP,
                    0f, 1f, 0f,
                    lastPos
            ));
        } else {
            pendingEvents.add(new GestureEvent(
                    GestureEvent.GestureType.DRAG,
                    moveDist, 1f, 0f,
                    pointers[pid].downPos
            ));
        }

        // Reset two-finger state if this pointer was part of it
        if (twoFinger.active && (twoFinger.finger1Id == pid || twoFinger.finger2Id == pid)) {
            twoFinger.active = false;
        }
    }

    // ------------------------------------------------------------------
    // Two-finger gesture detection
    // ------------------------------------------------------------------

    private void checkTwoFingerStart(int newPid) {
        // Find another active pointer
        for (int i = 0; i < pointers.length; i++) {
            if (i == newPid) {
                continue;
            }
            if (pointers[i].active) {
                twoFinger.finger1DownPos = pointers[newPid].downPos;
                twoFinger.finger2DownPos = pointers[i].downPos;
                twoFinger.finger1LastPos = pointers[newPid].lastPos;
                twoFinger.finger2LastPos = pointers[i].lastPos;
                twoFinger.initialDistance = twoFinger.finger2DownPos
                        .subtract(twoFinger.finger1DownPos).length();
                twoFinger.initialAngle = computeAngle(
                        twoFinger.finger1DownPos, twoFinger.finger2DownPos);
                twoFinger.finger1Id = newPid;
                twoFinger.finger2Id = i;
                twoFinger.active = true;
                return;
            }
        }
    }

    private void updateTwoFinger(int updatedPid, Vec2 newPos) {
        if (!twoFinger.active) {
            return;
        }

        if (twoFinger.finger1Id == updatedPid) {
            twoFinger.finger1LastPos = newPos;
        } else if (twoFinger.finger2Id == updatedPid) {
            twoFinger.finger2LastPos = newPos;
        } else {
            return;
        }

        float currentDistance = twoFinger.finger2LastPos
                .subtract(twoFinger.finger1LastPos).length();
        float scale = twoFinger.initialDistance > 0f
                ? currentDistance / twoFinger.initialDistance : 1f;

        float currentAngle = computeAngle(twoFinger.finger1LastPos, twoFinger.finger2LastPos);
        float angleDelta = currentAngle - twoFinger.initialAngle;

        // Emit pinch or rotate based on which is dominant
        float distanceChange = Math.abs(scale - 1f);
        float angleChange = Math.abs(angleDelta);

        if (distanceChange > angleChange * 0.01f) {
            // Pinch dominant
            pendingEvents.add(new GestureEvent(
                    GestureEvent.GestureType.PINCH,
                    0f, scale, 0f,
                    midpoint(twoFinger.finger1LastPos, twoFinger.finger2LastPos)
            ));
        } else if (angleChange > 1f) {
            // Rotate dominant
            pendingEvents.add(new GestureEvent(
                    GestureEvent.GestureType.ROTATE,
                    0f, 1f, angleDelta,
                    midpoint(twoFinger.finger1LastPos, twoFinger.finger2LastPos)
            ));
        }
    }

    private float computeAngle(Vec2 from, Vec2 to) {
        Vec2 dir = to.subtract(from);
        return (float) Math.toDegrees(Math.atan2(dir.y, dir.x));
    }

    private Vec2 midpoint(Vec2 a, Vec2 b) {
        return new Vec2(
                (a.x + b.x) / 2f,
                (a.y + b.y) / 2f
        );
    }

    /**
     * Get any pending gesture events without consuming them (for peeking).
     */
    public List<GestureEvent> getPendingEvents() {
        return Collections.unmodifiableList(pendingEvents);
    }
}
