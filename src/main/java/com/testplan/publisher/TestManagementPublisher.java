package com.testplan.publisher;

import com.testplan.model.TestPlan;

/**
 * Contract for sending generated assets to external test management tools.
 */
public interface TestManagementPublisher {
    PublishResult publish(TestPlan testPlan, PublishConfig config) throws Exception;
    String getName();
}
