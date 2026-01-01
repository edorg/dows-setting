package org.dows.setting.mock;

import org.dows.rfa.api.ResumeFileApi;
import org.dows.rfa.open.PostResumeFileEntityRequest;

public class ResumeFileApiMock implements ResumeFileApi {
    @Override
    public Long postEntity(PostResumeFileEntityRequest request) {
        return 0L;
    }
}
