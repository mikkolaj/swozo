import { ServiceModuleSummaryDto } from 'api';
import { Filters } from './ServiceModuleFilter';

const naiveTextCompare = (testedValue: string, matcher: string): boolean => {
    return testedValue.toLowerCase().includes(matcher.toLowerCase());
};

export const matchesTextSearch = (module: ServiceModuleSummaryDto, searchContent: string): boolean => {
    if (searchContent === '') return true;
    return (
        naiveTextCompare(module.creator.email, searchContent) || naiveTextCompare(module.name, searchContent)
    );
};

export const filterModules = (
    modules: ServiceModuleSummaryDto[],
    searchContent: string,
    filters: Filters
) => {
    return modules
        .filter((module) => filters.service === '' || module.serviceName === filters.service)
        .filter((module) => matchesTextSearch(module, searchContent));
};
