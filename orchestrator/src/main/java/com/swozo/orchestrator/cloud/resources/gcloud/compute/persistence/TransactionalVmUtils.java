package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmStatus;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class TransactionalVmUtils {
    private final VmRepository vmRepository;
    private final VmMapper vmMapper;

    public VmEntity save(VmAddress address) {
        return vmRepository.save(vmMapper.toPersistence(address));
    }

    public void updateStatus(VmEntity entity, VmStatus status) {
        entity.setStatus(status);
        vmRepository.save(entity);
    }

    public VmAddress toDto(VmEntity entity) {
        return vmMapper.toDto(entity);
    }

    public VmEntity toPersistence(VmAddress entity) {
        return vmMapper.toPersistence(entity);
    }
}
