package com.bridgelabz.fundoo.note.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundoo.note.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

public Note findByUserIdAndNoteId(long userId, long noteId);

public Note findByTitleAndDescription(String title, String description);
//public List<Note> findByOrderBytitle();
}
