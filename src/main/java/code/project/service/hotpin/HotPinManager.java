package code.project.service.hotpin;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
public class HotPinManager {

    private static final int MAX_PINS = 5;

    // 최신이 앞(헤드)로 오도록 관리
    private final Deque<Long> pins = new ArrayDeque<>();

    // 추가
    public synchronized void addPinIfAbsent(Long postId) {
        if (postId == null) return;
        if (pins.contains(postId)) {
            pins.remove(postId);
            pins.addFirst(postId);
        } else {
            pins.addFirst(postId);
            if (pins.size() > MAX_PINS) {
                pins.removeLast();
            }
        }
    }

    // 좋아요가 줄어들면 핀 제거
    public synchronized void removePin(Long postId) {
        if (postId == null) return;
        pins.remove(postId);
    }

    public synchronized List<Long> getPinsNewestFirst() {
        return new ArrayList<>(pins);
    }
}
