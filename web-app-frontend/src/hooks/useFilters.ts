import { useEffect, useState } from 'react';

export const useFilters = <Filters, Resource>(
    initialValues: Filters,
    resources: Resource[] | undefined,
    filterLogic: (resource: Resource[], filters: Filters, searchPhrase: string) => Resource[]
) => {
    const [filters, setFilters] = useState<Filters>(initialValues);
    const [searchPhrase, setSearchPhrase] = useState('');
    const [filteredResources, setFilteredResources] = useState<Resource[]>(resources ?? []);

    useEffect(() => {
        if (resources) {
            setFilteredResources(filterLogic(resources, filters, searchPhrase));
        }
    }, [searchPhrase, resources, filters, filterLogic]);

    return {
        filteredResources,
        filters,
        setFilters,
        searchPhrase,
        setSearchPhrase,
    };
};
