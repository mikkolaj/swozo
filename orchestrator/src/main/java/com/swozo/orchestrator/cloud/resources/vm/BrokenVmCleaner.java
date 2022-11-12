package com.swozo.orchestrator.cloud.resources.vm;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VmRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.swozo.utils.LoggingUtils.logAndDefault;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class BrokenVmCleaner implements ApplicationListener<ApplicationReadyEvent> {
    private final VmRepository vmRepository;
    private final TimedVmProvider vmProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        var deletedVms = deleteVmsWithBrokenMetadata();
        // TODO: delete VMs that have been created on cloud, but we didn't manage to register them (rather rare case)
        logger.info("Successfully deleted {} broken vms", deletedVms);
    }

    private long deleteVmsWithBrokenMetadata() {
        return vmRepository.findAllCreatedWithBrokenMetadata().stream()
                .map(vmEntity -> vmProvider.deleteInstance(vmEntity.getId())
                        .thenApply(successfullyDeletedInstanceStage -> true)
                        .exceptionally(logAndDefault(logger, String.format("Error while deleting %s", vmEntity), false))
                ).map(this::waitForResult)
                .filter(Boolean::booleanValue)
                .count();
    }

    private boolean waitForResult(CompletableFuture<Boolean> futureResult) {
        try {
            return futureResult.join();
        } catch (CompletionException | CancellationException ex) {
            logger.error("Failed to wait for deletion result", ex);
            return false;
        }
    }
}
