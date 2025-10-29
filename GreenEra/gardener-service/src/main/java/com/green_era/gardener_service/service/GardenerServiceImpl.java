package com.green_era.gardener_service.service;

import com.green_era.gardener_service.dto.GardenerDto;
import com.green_era.gardener_service.entity.GardenerEntity;
import com.green_era.gardener_service.repository.GardenerRepository;
import com.green_era.gardener_service.utils.AccountNotFoundException;
import com.green_era.gardener_service.utils.DuplicateAccountException;
import com.green_era.gardener_service.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GardenerServiceImpl implements GardenerService{

    @Autowired
    GardenerRepository gardenerRepository;
    @Override
    public GardenerDto registerGardener(GardenerDto gardenerDto) throws DuplicateAccountException {
        Optional<GardenerEntity> gardenerEntityOptionalEmail = gardenerRepository.findByEmail(gardenerDto.getEmail());
        if(gardenerEntityOptionalEmail.isPresent()) throw new DuplicateAccountException("Account already exists with given email id.");

        Optional<GardenerEntity> gardenerEntityOptionalPhone = gardenerRepository.findByPhoneNumber(gardenerDto.getPhoneNumber());
        if(gardenerEntityOptionalPhone.isPresent()) throw new DuplicateAccountException("Account already exists with given phone number.");

        GardenerEntity gardener = Mapper.gardenerDtoToEntity(gardenerDto);
        gardener = gardenerRepository.save(gardener);

        return Mapper.gardenerEntityToDto(gardener);
    }

    @Override
    public List<GardenerDto> getAllGardeners() {
        Iterable<GardenerEntity> listOfGardenersEntity = gardenerRepository.findAll();
        List<GardenerDto> listOfGardeners = new ArrayList<>();
        listOfGardenersEntity.forEach(gardener -> {
            listOfGardeners.add(Mapper.gardenerEntityToDto(gardener));
        });
        return listOfGardeners;
    }

    @Override
    public GardenerDto getGardenerById(Long id) throws AccountNotFoundException {
        Optional<GardenerEntity> optionalOfGardenerEntity = gardenerRepository.findById(id);
        if(optionalOfGardenerEntity.isPresent()){
            return Mapper.gardenerEntityToDto(optionalOfGardenerEntity.get());
        } else throw new AccountNotFoundException("No gardener found with the given id.");
    }

    @Override
    public GardenerDto getGardenerByEmail(String email) throws AccountNotFoundException {
        Optional<GardenerEntity> gardenerEntity = gardenerRepository.findByEmail(email);
        if(gardenerEntity.isPresent()) return Mapper.gardenerEntityToDto(gardenerEntity.get());
        else throw new AccountNotFoundException("Gardener not registered with given mail id.");
    }

    @Override
    public String deleteGardener(Long id) throws AccountNotFoundException {
        Optional<GardenerEntity> optionalOfGardenerEntity = gardenerRepository.findById(id);
        if(optionalOfGardenerEntity.isPresent()){
            gardenerRepository.delete(optionalOfGardenerEntity.get());
            return "Success";
        } else throw new AccountNotFoundException("No gardener found with the given id.");
    }

    @Override
    public List<GardenerDto> getAvailableGardeners(String locality, boolean availability) {
        List<GardenerEntity> listOfGardeners = gardenerRepository.findByLocalityAndIsAvailable(locality, availability);
        List<GardenerDto> gardeners = new ArrayList<>();
        listOfGardeners.forEach(gardener -> {
            gardeners.add(Mapper.gardenerEntityToDto(gardener));
        });

        return gardeners;
    }

    @Override
    public GardenerDto updateAvailability(Long id, Boolean available) throws AccountNotFoundException {
        Optional<GardenerEntity> optionalOfGardenerEntity = gardenerRepository.findById(id);
        if(optionalOfGardenerEntity.isPresent()){
            GardenerEntity gardener = optionalOfGardenerEntity.get();
            gardener.setAvailable(available);
            gardener = gardenerRepository.save(gardener);

            return Mapper.gardenerEntityToDto(gardener);
        } else throw new AccountNotFoundException("No gardener found with the given id.");
    }

    @Override
    public String updateGardener(Long id, GardenerDto dto) throws AccountNotFoundException {
        Optional<GardenerEntity> optionalOfGardenerEntity = gardenerRepository.findById(id);
        if(optionalOfGardenerEntity.isPresent()){
            GardenerEntity gardener = Mapper.gardenerDtoToEntity(dto);
            gardenerRepository.save(gardener);
            return "Success";
        } else throw new AccountNotFoundException("No gardener found with the given id.");
    }

    @Override
    public GardenerDto markUnavailableByEmail(String email) throws AccountNotFoundException {
        Optional<GardenerEntity> gardenerEntity = gardenerRepository.findByEmail(email);
        if(gardenerEntity.isPresent()){
            GardenerEntity gardener = gardenerEntity.get();
            gardener.setAvailable(false);
            gardenerRepository.save(gardener);
            return Mapper.gardenerEntityToDto(gardener);
        }else throw new AccountNotFoundException("No gardener found with given email id.");
    }
}
