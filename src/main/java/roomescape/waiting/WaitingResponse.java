package roomescape.waiting;

public record WaitingResponse(
        Long id,
        String date,
        String themeName,
        String timeValue
) {}
