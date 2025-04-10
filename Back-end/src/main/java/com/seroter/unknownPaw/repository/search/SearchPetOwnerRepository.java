package com.seroter.unknownPaw.repository.search;

import com.seroter.unknownPaw.entity.PetOwner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPetOwnerRepository {
    Page<PetOwner> searchDynamic(String keyword, String location, String category, Pageable pageable);
}
