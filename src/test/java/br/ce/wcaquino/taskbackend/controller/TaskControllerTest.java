package br.ce.wcaquino.taskbackend.controller;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;

public class TaskControllerTest {
	
	@Mock
	private TaskRepo todoRepo;
	
	@InjectMocks
	private TaskController controller;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void naoDeveSalvarTarefaSemDescricao() {
		
		Task task = new Task();
		task.setDueDate(LocalDate.now());
		
		try {
			controller.save(task);
			Assert.fail("Não deveria ter chegado nesse ponto!");
		} catch (ValidationException e) {
			assertEquals("Fill the task description", e.getMessage());
		}
		
	}
	
	@Test
	public void naoDeveSalvarTarefaSemData() {
		Task task = new Task();
		task.setTask("task");
		
		try {
			controller.save(task);
			Assert.fail("Não deveria ter chegado nesse ponto!");
		} catch (ValidationException e) {
			assertEquals("Fill the due date", e.getMessage());
		}
	}
	
	@Test
	public void naoDeveSalvarTarefaComDataPassada() {
		Task task = new Task();
		task.setDueDate(LocalDate.of(2010, 01, 01));
		task.setTask("task");
		
		try {
			controller.save(task);
			Assert.fail("Não deveria ter chegado nesse ponto!");
		} catch (ValidationException e) {
			assertEquals("Due date must not be in past", e.getMessage());
		}
	}
	
	@Test
	public void deveSalvarTarefaComSucesso() throws ValidationException {
		Task task = new Task();
		task.setDueDate(LocalDate.now());
		task.setTask("task");
		controller.save(task);
		
		Mockito.verify(todoRepo).save(task);
		
	}
}
