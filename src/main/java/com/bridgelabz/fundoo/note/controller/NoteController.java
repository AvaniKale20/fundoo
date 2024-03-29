package com.bridgelabz.fundoo.note.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoo.note.dto.CollaboratorDTO;
import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.service.NoteService;
import com.bridgelabz.fundoo.response.Response;

@RestController
@RequestMapping("/user")

@PropertySource("classpath:message.properties")
public class NoteController {

	@Autowired
	private NoteService noteService;

	@PostMapping("/createNote")
	//ResponseEntity extends HttpEntity<T>
	//Extension of HttpEntity that adds a HttpStatus status code
	//consist header,status code,body
	public ResponseEntity<Response> creatingNote(@RequestBody NoteDTO noteDto, @RequestHeader String token) {
		System.out.println("NotesController.creatingNote()");
		Response responseStatus = noteService.createNote(noteDto, token);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}

	@PutMapping("/updateNote")
	public ResponseEntity<Response> updatingNote(@RequestBody NoteDTO noteDto, @RequestHeader String token,
			@RequestParam long noteId) {

		Response responseStatus = noteService.updateNote(noteDto, token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.ACCEPTED);
	}

	@PutMapping("/retrieveNote")
	public ResponseEntity<Response> retrievingNote1(@RequestHeader String token, @RequestParam long noteId) {

		Response responseStatus = noteService.retrieveNote(token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}

	@PutMapping("/deleteNote")
	public ResponseEntity<Response> deletingNote(@RequestHeader String token, @RequestParam long noteId) {
		Response responseStatus = noteService.deleteNote(token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}

	@PutMapping("/permenantlyDeleted")
	public ResponseEntity<Response> permenantdeletingNote(@RequestHeader String token, @RequestParam long noteId) {
		Response responsestatus = noteService.deleteNotePermenantly(token, noteId);
		return new ResponseEntity<Response>(responsestatus, HttpStatus.OK);

	}
	
	@PutMapping("/pinned")
	public ResponseEntity<Response> pinnedOrNot(@RequestHeader String token ,@RequestParam long noteId)
	{
		Response responseStatus = noteService.checkPinOrNot(token, noteId);
		return new ResponseEntity<Response>(responseStatus,HttpStatus.OK);	
	}
	
	@PutMapping("/archieved")
	public ResponseEntity<Response> archievedOrNot(@RequestHeader String token, @RequestParam long noteId)
	{
		Response responseStatus = noteService.checkArchieveOrNot(token, noteId);
		return new ResponseEntity<Response>(responseStatus,HttpStatus.OK);	
	}
	
	@PutMapping("/color")
	public ResponseEntity<Response> colorSet(@RequestHeader String token, @RequestParam long noteId, @RequestParam String color)
	{
		Response responseStatus = noteService.setColour(token, noteId, color);
		return new ResponseEntity<Response>(responseStatus,HttpStatus.OK);
		
	}
	
	@GetMapping("/getTrash")
	public List<Note> getTrashNotes(@RequestHeader String token) {
		List<Note> listnotes = noteService.restoreTrashNotes(token);
		return listnotes;
	}
	
	@GetMapping("/getPin")
	public List<Note> getPinnedNotes(@RequestHeader String token) {
		List<Note> listnotes = noteService.getPinnedNote(token);
		return listnotes;
	}	@PutMapping("/retrieveNote")
	public ResponseEntity<Response> retrievingNote(@RequestHeader String token, @RequestParam long noteId) {

		Response responseStatus = noteService.retrieveNote(token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}

	
	@GetMapping("/getArchieve")
	public List<Note> getArchieveNotes(@RequestHeader String token) {
		List<Note> listnotes = noteService.getArchievedNote(token);
		return listnotes;
	}
	@PutMapping("/setRemainder")
	public ResponseEntity<Response> setRemainderToNote(@RequestHeader String token ,@RequestParam long noteId,@RequestParam String time)
	{
		Response response = noteService.setReminder(token, noteId, time);
		
		return new ResponseEntity<Response>(response,HttpStatus.OK);
		
	}
	
	@PutMapping("/deleteReminder")
	public ResponseEntity<Response> deleteRemainderToNote(@RequestHeader String token, @RequestParam long noteId)
	{
		Response response = noteService.deleteReminder(token, noteId);
		return new ResponseEntity<Response>(response,HttpStatus.OK);
	}
	@GetMapping("/getSorted")
	public List<Note> sortByTitle(@RequestHeader String token)
	{
		List<Note> listnotes = noteService.sortByTitle(token);
		return listnotes;
	}
	@PutMapping("/addCollaborator")
	public ResponseEntity<Response> addCollaborator(@RequestHeader String token, @RequestParam long noteId , @RequestBody CollaboratorDTO collaboratordto)
	{
		Response response = noteService.addCollaboratorToNote(token, noteId, collaboratordto);
		return new ResponseEntity<Response>(response,HttpStatus.OK);		
	}
	
	@PutMapping("/deleteCollaborator")
	public ResponseEntity<Response> deleteCollaborator(@RequestHeader String token , @RequestParam long noteId,@RequestParam String emailId)
	{
		Response response = noteService.deleteCollaboratorToNote(token, noteId, emailId);
		return new ResponseEntity<Response>(response,HttpStatus.OK);
	}
}
