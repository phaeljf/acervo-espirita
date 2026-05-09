package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Location;
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

    // Cria uma nova localização
    @Transactional
    public Location createLocation(Location location, User createdBy) {

        if (locationRepository.existsByShelfAndPosition(location.getShelf(), location.getPosition())) {
            throw new IllegalArgumentException("Já existe uma localização com essa estante e posição.");
        }

        Location savedLocation = locationRepository.save(location);
        logService.register(LogType.LOCATION_CREATED, createdBy,"Localização "
                                                                + savedLocation.getShelf()
                                                                + "-" + savedLocation.getPosition()
                                                                + " foi criada."
        );

        return savedLocation;
    }

    // Atualiza localização
    @Transactional
    public Location updateLocation(Long id, String shelf, String position, User updatedBy) {

        Location location = findById(id);
        boolean locationAlreadyExists = locationRepository.existsByShelfAndPosition(shelf, position);

        boolean sameLocation = location.getShelf().equals(shelf) && location.getPosition().equals(position);

        if (locationAlreadyExists && !sameLocation) {
            throw new IllegalArgumentException("Já existe uma localização com essa estante e posição.");
        }

        location.setShelf(shelf);
        location.setPosition(position);

        Location updatedLocation = locationRepository.save(location);

        logService.register(LogType.LOCATION_UPDATED, updatedBy,"Localização "
                                                                + updatedLocation.getShelf()
                                                                + "-"
                                                                + updatedLocation.getPosition()
                                                                + " foi atualizada."
        );

        return updatedLocation;
    }

    // Remove localização
    @Transactional
    public void deleteLocation(Long id, User deletedBy) {

        Location location = findById(id);

        if (bookCopyRepository.existsByLocation(location)) {
            throw new IllegalStateException("Não é possível remover localização em uso.");
        }

        locationRepository.delete(location);

        logService.register(LogType.LOCATION_DELETED, deletedBy,"Localização "
                                                                + location.getShelf()
                                                                + "-"
                                                                + location.getPosition()
                                                                + " foi removida."
        );
    }

    // Busca localização por id
    @Transactional(readOnly = true)
    public Location findById(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Localização não encontrada."));
    }

    // Lista todas as localizações
    @Transactional(readOnly = true)
    public List<Location> findAll() {
        return locationRepository.findAll();
    }
}