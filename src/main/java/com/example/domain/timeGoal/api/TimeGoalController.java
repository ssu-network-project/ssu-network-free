package com.example.domain.timeGoal.api;

import com.example.domain.timeGoal.dto.PostTimeGoalsReq;
import com.example.domain.timeGoal.dto.PostTimeGoalsRes;
import com.example.domain.timeGoal.service.TimeGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/TimeGoal")
public class TimeGoalController {

    private final TimeGoalService usedTimeService;
    @PostMapping("/goals/{userId}")
    public PostTimeGoalsRes createGoalsByDomainName(
            @RequestBody PostTimeGoalsReq postTimeGoals,
            @PathVariable(name="userId")Long userIdx){
        return usedTimeService.createGoalsByDomainName(postTimeGoals, userIdx);
    }
}
