package com.swozo.api.web.mda.vm;

import com.swozo.api.web.mda.vm.dto.VmDto;
import com.swozo.api.web.mda.vm.request.CreateVmRequest;
import com.swozo.api.web.mda.vm.request.EdtiVmRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/vms")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class VmController {
    private final Logger logger = LoggerFactory.getLogger(VmController.class);
    private final VmService vmService;

    @GetMapping("/all-system-vms")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<VmDto> allSystemVms(AccessToken token) {
        logger.info("getting all vms");
        return vmService.getAllSystemVmsDto();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public VmDto addVm(AccessToken token, @RequestBody CreateVmRequest createVmRequest) {
        logger.info("creating new vm");
        return vmService.createVm(createVmRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVm(AccessToken token, @PathVariable Long id) {
        logger.info("deleting vm with id: {}", id);
        vmService.deleteVm(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VmDto editVm(AccessToken token, @PathVariable Long id, @RequestBody EdtiVmRequest edtiVmRequest) {
        logger.info("editing vm with id: {}", id);
        return vmService.editVm(id, edtiVmRequest);
    }
}
