package com.type.multi_typer.repository;

import com.type.multi_typer.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    Optional<Room> findRoomByCode(String code);
    List<Room> findByLastActivityAtBefore(LocalDateTime cutoff);
}
