package com.swozo.orchestrator.cloud.resources.vm;

import com.swozo.model.Psm;

import java.util.concurrent.Future;

public interface VMProvider {
  Future<VMDetails> createInstance(Psm psm) throws InterruptedException, VMOperationFailed;

  Future<VMDeleted> deleteInstance(String vmName) throws InterruptedException, VMOperationFailed;
}
