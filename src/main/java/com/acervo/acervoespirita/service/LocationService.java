package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Location;
import com.acervo.acervoespirita.model.Shelf;
import com.acervo.acervoespirita.model.ShelfPosition;
import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.repository.BookCopyRepository;
import com.acervo.acervoespirita.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LogService logService;

    @Transactional
    public Location createLocation(Location location, User createdBy) {

        if (locationRepository.existsByShelfAndShelfPosition(location.getShelf(), location.getShelfPosition())) {
            throw new IllegalArgumentException("Já existe uma localização com essa estante e prateleira.");
        }

        Location savedLocation = locationRepository.save(location);
        logService.register(LogType.LOCATION_CREATED, createdBy, "Localização " + savedLocation.getLocation() + " foi criada.");

        return savedLocation;
    }

    @Transactional
    public Location updateLocation(Long id, Shelf shelf, ShelfPosition shelfPosition, User updatedBy) {

        Location location = findById(id);
        boolean locationAlreadyExists = locationRepository.existsByShelfAndShelfPosition(shelf, shelfPosition);
        boolean sameLocation = location.getShelf().equals(shelf) && location.getShelfPosition().equals(shelfPosition);

        if (locationAlreadyExists && !sameLocation) {
            throw new IllegalArgumentException("Já existe uma localização com essa estante e prateleira.");
        }

        location.setShelf(shelf);
        location.setShelfPosition(shelfPosition);
        Location updatedLocation = locationRepository.save(location);
        logService.register(LogType.LOCATION_UPDATED, updatedBy, "Localização " + updatedLocation.getLocation() + " foi atualizada.");

        return updatedLocation;
    }

    @Transactional
    public void deleteLocation(Long id, User deletedBy) {

        Location location = findById(id);
        if (bookCopyRepository.existsByLocation(location)) {
            throw new IllegalStateException("Não é possível remover localização em uso.");
        }
        locationRepository.delete(location);
        logService.register(LogType.LOCATION_DELETED, deletedBy, "Localização " + location.getLocation() + " foi removida.");
    }

    @Transactional(readOnly = true)
    public Location findById(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Localização não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Location> findAll() {
        return locationRepository.findAll();
    }
}