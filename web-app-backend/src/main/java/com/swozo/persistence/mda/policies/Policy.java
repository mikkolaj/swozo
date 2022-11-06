package com.swozo.persistence.mda.policies;

import com.swozo.api.web.exceptions.types.mda.PolicyNotMetException;
import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.mda.vminfo.PimVmInfo;
import com.swozo.persistence.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Policies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Policy extends BaseEntity {
    private PolicyType policyType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    @ToString.Exclude
    private User teacher;

    private Integer value;

    public void checkPolicy(PimVmInfo pimVmInfo) {
        switch (this.policyType){
            case MAX_VCPU:
                if (pimVmInfo.getVcpu() > value)
                    throw PolicyNotMetException.withBrokenPolicy(policyType, value, pimVmInfo.getVcpu());
                else
                    break;
            case MAX_RAM:
                if (pimVmInfo.getRam() > value)
                    throw PolicyNotMetException.withBrokenPolicy(policyType, value, pimVmInfo.getRam());
                else
                    break;
            case MAX_DISK:
                if (pimVmInfo.getDisk() > value)
                    throw PolicyNotMetException.withBrokenPolicy(policyType, value, pimVmInfo.getDisk());
                else
                    break;
            case MAX_BANDWIDTH:
                if (pimVmInfo.getBandwidth() > value)
                    throw PolicyNotMetException.withBrokenPolicy(policyType, value, pimVmInfo.getBandwidth());
                else
                    break;
        }
    }
}
