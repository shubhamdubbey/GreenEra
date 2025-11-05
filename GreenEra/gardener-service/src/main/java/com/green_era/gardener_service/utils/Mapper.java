package com.green_era.gardener_service.utils;

import com.green_era.gardener_service.dto.GardenerDto;
import com.green_era.gardener_service.entity.GardenerEntity;

public class Mapper {

    public static GardenerEntity gardenerDtoToEntity(GardenerDto dto){
        GardenerEntity gardener = new GardenerEntity();
        gardener.setAvailable(dto.getAvailable());
        gardener.setName(dto.getName());
        gardener.setLocality(dto.getLocality());
        gardener.setPhoneNumber(dto.getPhoneNumber());
        gardener.setEmail(dto.getEmail());
        gardener.setHourlyRate(dto.getHourlyRate());
        gardener.setRating(dto.getRating());
        gardener.setTotalJobsCompleted(dto.getJobsCompleted());
        gardener.setWorkEndTime(dto.getWorkEndTime());
        gardener.setWorkStartTime(dto.getWorkStartTime());
        return gardener;
    }

    public static GardenerDto gardenerEntityToDto(GardenerEntity entity)
    {
        GardenerDto dto = new GardenerDto();

        dto.setAvailable(entity.isAvailable());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocality(entity.getLocality());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setRating(entity.getRating());
        dto.setJobsCompleted(entity.getTotalJobsCompleted());
        dto.setHourlyRate(entity.getHourlyRate());
        dto.setWorkEndTime(entity.getWorkEndTime());
        dto.setWorkStartTime(entity.getWorkStartTime());
        return dto;
    }
}
