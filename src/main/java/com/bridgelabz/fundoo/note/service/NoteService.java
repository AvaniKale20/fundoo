package com.bridgelabz.fundoo.note.service;

import java.util.List;

import com.bridgelabz.fundoo.note.dto.CollaboratorDTO;
import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.response.Response;

public interface NoteService {

	public Response createNote(NoteDTO notedto, String token);
	public Response updateNote(NoteDTO notedto, String token, long noteId);	
	public Response retrieveNote(String token, long noteId);
	public Response deleteNote(String token, long noteId);
	public Response deleteNotePermenantly(String token, long noteId);
	public Response checkPinOrNot(String token, long noteId);
	public List<Note> restoreTrashNotes(String token);
	public Response checkArchieveOrNot(String token, long noteId);
	public Response setColour(String token, long noteId, String color);
	public List<Note> getPinnedNote(String token);
	public List<Note> getArchievedNote(String token);
	public Response addCollaboratorToNote(String token, long noteId, CollaboratorDTO collaboratordto);
	public Response deleteCollaboratorToNote(String token, long noteId, String emailId);
	Note findNoteFromUser(String title, String description);
	Response setReminder(String token, long noteId, String time);
	Response deleteReminder(String token, long noteId);
	List<Note> sortByTitle(String token);
	
	
}