package code.project.util;

import code.project.dto.FacilityBusinessHourDTO;

import java.time.*;
import java.util.*;

public final class OpenTimeUtil {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final Map<DayOfWeek, String> DKEY = Map.of(
            DayOfWeek.MONDAY, "MON",
            DayOfWeek.TUESDAY, "TUE",
            DayOfWeek.WEDNESDAY, "WED",
            DayOfWeek.THURSDAY, "THU",
            DayOfWeek.FRIDAY, "FRI",
            DayOfWeek.SATURDAY, "SAT",
            DayOfWeek.SUNDAY, "SUN"
    );

    private OpenTimeUtil() {}

    public static boolean isOpenNow(List<FacilityBusinessHourDTO> rows) {
        return isOpenAt(rows, ZonedDateTime.now(KST));
    }

    public static boolean isOpenAt(List<FacilityBusinessHourDTO> rows, ZonedDateTime nowKst) {
        if (rows == null || rows.isEmpty()) return false;

        String today = DKEY.get(nowKst.getDayOfWeek());
        String yesterday = DKEY.get(nowKst.minusDays(1).getDayOfWeek());

        // 오늘 레코드
        List<FacilityBusinessHourDTO> todays = filterByDay(rows, today);
        // 오늘 중 24시간 하나라도 있으면 영업중
        if (todays.stream().anyMatch(r -> !r.isClosed() && r.isOpen24h())) return true;
        // 오늘 구간 포함되면 영업중
        if (todays.stream().anyMatch(r -> containsNow(r, nowKst.toLocalTime()))) return true;

        // 어제에서 자정 넘김 구간인지 확인 (예: 어제 22:00~오늘 02:00)
        List<FacilityBusinessHourDTO> yRows = filterByDay(rows, yesterday);
        return yRows.stream().anyMatch(r -> containsNowFromYesterday(r, nowKst.toLocalTime()));
    }

    private static List<FacilityBusinessHourDTO> filterByDay(List<FacilityBusinessHourDTO> rows, String dayKey) {
        return rows.stream().filter(r -> {
            List<String> days = r.getDays();
            if (days == null) return false;
            for (String d : days) {
                if (dayKey.equalsIgnoreCase(String.valueOf(d).trim())) return true;
            }
            return false;
        }).toList();
    }

    private static boolean containsNow(FacilityBusinessHourDTO r, LocalTime now) {
        if (r == null || r.isClosed()) return false;
        if (r.isOpen24h()) return true;

        LocalTime s = parseHHMM(r.getOpenTime());
        LocalTime e = parseHHMM(r.getCloseTime());
        if (s == null || e == null) return false;
        if (e.equals(s)) return false; // 0분 영업은 미운영 취급

        // 일반 구간: s < e
        if (e.isAfter(s)) {
            return (!now.isBefore(s)) && now.isBefore(e);
        }
        // 자정 넘김: s > e (예: 22:00~02:00)
        return (!now.isBefore(s)) || now.isBefore(e);
    }

    private static boolean containsNowFromYesterday(FacilityBusinessHourDTO r, LocalTime now) {
        if (r == null || r.isClosed()) return false;
        if (r.isOpen24h()) return true;

        LocalTime s = parseHHMM(r.getOpenTime());
        LocalTime e = parseHHMM(r.getCloseTime());
        if (s == null || e == null) return false;

        // 자정 넘김(어제 s > e)만 오늘 새벽까지 이어짐
        if (!s.isAfter(e)) return false;
        // 오늘 00:00~e 사이면 어제 구간의 연장으로 영업중
        return now.isBefore(e);
    }

    private static LocalTime parseHHMM(String hhmm) {
        if (hhmm == null) return null;
        String s = hhmm.trim();
        if (s.isEmpty() || "00:00".equals(s)) return "00:00".equals(s) ? LocalTime.MIDNIGHT : null;
        try {
            // 기대 포맷 "HH:mm"
            return LocalTime.parse(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}
