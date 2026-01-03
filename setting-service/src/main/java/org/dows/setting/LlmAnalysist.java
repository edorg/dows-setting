package org.dows.setting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LlmAnalysist implements Analysisable {


    public void analysis(String text) {
        log.info("LlmAnalysist:{}", text);

    }
}
