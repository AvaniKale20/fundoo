package com.bridgelabz.fundoo.note.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.model.User;
import com.bridgelabz.fundoo.note.dto.CollaboratorDTO;
import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.note.model.Collaborator;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.repository.*;
import com.bridgelabz.fundoo.note.repository.CollaboratorRepository;
import com.bridgelabz.fundoo.repository.UserRepository;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.model.EmailId;
//import com.bridgelabz.fundoo.model.User;
//import com.bridgelabz.fundoo.repository.*;
import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.bridgelabz.fundoo.utility.TokenUtil;
import com.bridgelabz.fundoo.utility.Utility;

@Service("notesService")
@PropertySource("classpath:message.properties")
public class NotesServiceImpl implements NoteService {

	@Autowired
	private TokenUtil userToken;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private Environment environment;
	
	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Override
	public Response createNote(NoteDTO noteDto, String token) {

		long id = userToken.decodeToken(token);
		 token=userToken.createToken(id);
		 System.out.println(token);
		System.out.println("id"+id);

		if (noteDto.getTitle().isEmpty() && noteDto.getDescription().isEmpty()) {

			throw new UserException(-5, "Title and description are empty");

		}
		Note note = modelMapper.map(noteDto, Note.class);
		Optional<User> user = userRepository.findById(id);
		note.setUserId(id);
		note.setCreated(LocalDateTime.now());
		note.setModified(LocalDateTime.now());
		user.get().getNotes().add(note);
		noteRepository.save(note);
		userRepository.save(user.get());

		Response response = ResponseHelper.statusResponse(100,
				environment.getProperty("status.notes.createdSuccessfull"));
		return response;

	}

	@Override
	public Response updateNote(NoteDTO noteDto, String token, long noteId) {
		if (noteDto.getTitle().isEmpty() && noteDto.getDescription().isEmpty()) {
			throw new UserException(-5, "Title and description are empty");
		}

		long id = userToken.decodeToken(token);
		Note note = noteRepository.findByUserIdAndNoteId(id, noteId);
		note.setTitle(noteDto.getTitle());
		note.setDescription(noteDto.getDescription());
		note.setModified(LocalDateTime.now());
		noteRepository.save(note);

		Response response = ResponseHelper.statusResponse(200,
				environment.getProperty("status.notes.updatedSuccessfull"));
		return response;
	}

	@Override
	public Response retrieveNote(String token, long noteId) {

		long id = userToken.decodeToken(token);
		Note note = noteRepository.findByUserIdAndNoteId(id, noteId);
		String title = note.getTitle();
		System.out.println(title);

		String description = note.getDescription();
		System.out.println(description);

		Response response = ResponseHelper.statusResponse(300, "retrieved successfully");
		return response;
	}

	public Response deleteNote(String token, long noteId) {
		long id = userToken.decodeToken(token);

		Note note = noteRepository.findByUserIdAndNoteId(id, noteId);

		if (note.isTrash() == false) {
			note.setTrash(true);
			note.setModified(LocalDateTime.now());
			noteRepository.save(note);
			Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.trashed"));
			return response;
		}

		Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.trashError"));
		return response;
	}

	public Response deleteNotePermenantly(String token, long noteId) {

		long id = userToken.decodeToken(token);

		Optional<User> user = userRepository.findById(id);
		Note note = noteRepository.findByUserIdAndNoteId(id, noteId);

		if (note.isTrash() == true) {
			user.get().getNotes().remove(note);
			userRepository.save(user.get());
			noteRepository.delete(note);
			Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.deleted"));
			return response;
		} else {
			Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.noteDeleted"));
			return response;
		}
	}

	@Override
	public Response checkPinOrNot(String token, long noteId) {

		long userId = userToken.decodeToken(token);
		System.out.println(userId);
		Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);
		System.out.println("note is" + note);

		if (note == null) {
			throw new UserException(100, "note is not exist");
		}

		if (note.isPin() == false) {
			note.setPin(true);
			noteRepository.save(note);

			Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.pinned"));
			return response;
		} else {
			note.setPin(false);
			noteRepository.save(note);
			Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.unpinned"));
			return response;
		}
	}

	@Override
	public Response checkArchieveOrNot(String token, long noteId) {

		long userId = userToken.decodeToken(token);
		Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);

		if (note == null) {
			throw new UserException(100, "note is not exist");
		}

		if (note.isArchieve() == false) {
			note.setArchieve(true);
			noteRepository.save(note);

			Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.archieved"));
			return response;
		} else {
			note.setArchieve(false);
			noteRepository.save(note);

			Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.unarchieved"));
			return response;
		}

	}

	@Override
	public Response setColour(String token, long noteId, String color) {

		long userId = userToken.decodeToken(token);
		Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);
		if (note == null) {
			throw new UserException(100, "invalid note or not exist");
		}

		note.setColour(color);
		noteRepository.save(note);

		Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.color"));
		return response;
	}

	
	@Override
	public List<Note> restoreTrashNotes(String token) {

		long userId = userToken.decodeToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException("Sorry ! Note are not available"));
		List<Note> userNote = user.getNotes().stream().filter(data -> (data.isTrash() == true))
				.collect(Collectors.toList());
		
		return userNote;

	}


	@Override
	public List<Note> getPinnedNote(String token) {
		long userId = userToken.decodeToken(token);

		User user = userRepository.findById(userId).orElseThrow(() -> new UserException("No note is available"));
		List<Note> pinNote = user.getNotes().stream().filter(data -> (data.isPin() == true))
				.collect(Collectors.toList());
		user.getNotes().stream().filter(data1 -> (data1.isPin() == true)).collect(Collectors.toList()).forEach(System.out::println);
		
		return pinNote;

	}
	
	@Override
	public List<Note> getArchievedNote(String token) {
		long userId = userToken.decodeToken(token);

		User user = userRepository.findById(userId).orElseThrow(() -> new UserException("No note is available"));
		List<Note> archieveNote = user.getNotes().stream().filter(data -> (data.isArchieve() == true))
				.collect(Collectors.toList());

		user.getNotes().stream().filter(data1 -> (data1.isArchieve() == true)).collect(Collectors.toList()).forEach(System.out::println);
		return archieveNote;

	}
	
	@Override
	public Note findNoteFromUser(String title, String description) {

		Note note = noteRepository.findByTitleAndDescription(title, description);
		System.out.println(note);
		if (note == null) {
			throw new UserException(-6, "note is not available");
		}

		return note;
	}

	public Response setReminder(String token, long noteId, String time) {
		long userId = userToken.decodeToken(token);
		Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);

		if (note == null) {
			throw new UserException(-5, "invalid note");
		}

		System.out.println("time is" + time);
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTime = LocalDateTime.parse(time, datetimeFormatter);
		System.out.println(localDateTime);
		LocalDateTime CurrentDateAndTime = LocalDateTime.now();
		System.out.println(CurrentDateAndTime);
		if (CurrentDateAndTime.compareTo(localDateTime) < 0) {
			note.setRemainder(localDateTime);
			noteRepository.save(note);
			Response response = ResponseHelper.statusResponse(100, environment.getProperty("note.status.remainder"));
			return response;
		}

		Response response = ResponseHelper.statusResponse(101, environment.getProperty("note.status.remainderfail"));

		return response;
	}
	
	public Response deleteReminder(String token, long noteId) {
		long userId = userToken.decodeToken(token);
		Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);
		if (note == null) {
			throw new UserException(-5, "note invalid");
		}

		note.setRemainder(null);
		noteRepository.save(note);

		Response response = ResponseHelper.statusResponse(100, environment.getProperty("note.status.deleteRemainder"));

		return response;

	}
	
		@Override
		public Response addCollaboratorToNote(String token, long noteId, CollaboratorDTO collaboratordto) {
			EmailId emailId = new EmailId();
			long userId = userToken.decodeToken(token);

			Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);

			if (note == null) {
				throw new UserException(-5, "note invalid");
			}
			Optional<Collaborator> userCollaborator = collaboratorRepository.findByEmailId(collaboratordto.getEmailId());

			if (userCollaborator.isPresent()) {
				throw new UserException(-5, "collaborator already present");
			}

			Collaborator collaborator = modelMapper.map(collaboratordto, Collaborator.class);
			Optional<User> mainUser = userRepository.findById(userId);
			Optional<User> collaborateUser = userRepository.findByEmailId(collaboratordto.getEmailId());
			System.out.println(collaborateUser);
			collaborator.setEmailId(collaboratordto.getEmailId());
			collaborator.setNoteId(noteId);
			collaborator.setUserId(userId);
			collaborator.setCreatedAt(LocalDateTime.now());

			collaborateUser.get().getCollaboratedNotes().add(note);
			
			note.getCollaboratedUser().add(collaborateUser.get());

			collaboratorRepository.save(collaborator);
			noteRepository.save(note);

			emailId.setFrom("earthjain143@gmail.com");
			emailId.setTo(collaboratordto.getEmailId());
			emailId.setSubject("Note collaborate to user");
			emailId.setBody("note collaboration from" + mainUser.get().getEmailId() + "to collaborating to"
					+ collaboratordto.getEmailId() + " for following note : title" + note.getTitle() + "and Description is"
					+ note.getDescription());

			Utility.sendEmail(emailId);

			Response response = ResponseHelper.statusResponse(100, environment.getProperty("collaborator.status.create"));

			return response;

		}

		@Override
		public Response deleteCollaboratorToNote(String token, long noteId, String emailId) {
			long userId = userToken.decodeToken(token);
			Optional<User> user = userRepository.findByEmailId(emailId);

			if (!user.isPresent()) {
				throw new UserException(-5, "user is not exist");
			}

			Note note = noteRepository.findByUserIdAndNoteId(userId, noteId);
			if (note == null) {
				throw new UserException(-5, "note is not exist");
			}

			Optional<Collaborator> collaborator = collaboratorRepository.findByEmailId(emailId);
			if (collaborator == null) {
				throw new UserException(-5, "collaborator is not exist");
			}
			
			user.get().getCollaboratedNotes().remove(note);
			note.getCollaboratedUser().remove(user.get());

			collaboratorRepository.delete(collaborator.get());
			userRepository.save(user.get());
			noteRepository.save(note);

			Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.collaborator.deleted"));
			return response;
		}
//	
		public List<Note> sortByTitle(String token)
		{
			
//			NoteRepository.findAll(Sort.by("title"));
//			NoteRepository.findAll(Sort.by("title").ascending().and(Sort.by("date").descending()));
		
			long userId = userToken.decodeToken(token);
			Optional<User> user = userRepository.findById(userId);

			if (!user.isPresent()) {
				throw new UserException(-5, "user is not exist");
			}
			List<Note> noteList=noteRepository.findAll();
			noteList.sort(Comparator.comparing(Note::getTitle));

			noteList.forEach(System.out::println);
			
		return noteList;
			
		}
		
//		public List<Note> sortByTitleASC(String token)
//		{
//
//			long userId = userToken.decodeToken(token);
//			Optional<User> user = userRepository.findById(userId);
//
//			if (!user.isPresent()) {
//				throw new UserException(-5, "user is not exist");
//			}
//			List<Note> noteList=noteRepository.findAll();
//		//	noteList.sort(Comparator.comparing(Note::getTitle));
//			Collections.sort(noteList, Comparator.comparing((Note) -> Note.getTitle()));
//			noteList.stream().forEach(System.out::println);
//			
//			return noteList;
//			
//		}
//		@Override
//		public List<Note> sortByDate(String token)
//		{
//			long userId = userToken.decodeToken(token);
//			Optional<User> user = userRepository.findById(userId);
//
//			if (!user.isPresent()) {
//				throw new UserException(-5, "user is not exist");
//			}
//			List<Note> noteList=noteRepository.findAll();
//			noteList.sort(Comparator.comparing(Note::getCreated));
//			return noteList;
//			
//		}
}
