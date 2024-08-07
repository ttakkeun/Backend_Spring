package ttakkeun.ttakkeun_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.CalendarResponseDto;
import ttakkeun.ttakkeun_server.service.CalendarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{year}/{month}/{date}")
    public ApiResponse<CalendarResponseDto> getCalendarData(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date) {
        CalendarResponseDto result = calendarService.getCalendarData(year, month, date);
        return ApiResponse.onSuccess(result);
    }
}
