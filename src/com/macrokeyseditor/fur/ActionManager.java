package com.macrokeyseditor.fur;

import java.util.Date;
import java.util.Stack;

/**
 * Classe che raccoglie e gestisce le azioni
 */
public final class ActionManager {
	
	private final Stack<Action> undo = new Stack<>();
	private final Stack<Action> redo = new Stack<>();
	
	
	public ActionManager() {
		
	}
	
	
	/**
	 * Esegue l'azione indicata e aggiunge la aggiunge alla
	 * lista delle azioni compiute
	 * @param a Azione da aggiungere; non null
	 * @throws NullPointerException Se {@code a} è null
	 * @see Action#execute()
	 */
	public void add(Action a) {
		add(a, true);
	}
	
	/**
	 * Aggiunge la aggiunge alla lista delle azioni compiute
	 * @param a Azione da aggiungere; non null
	 * @param execute True se l'azione deve essere eseguita
	 * @throws NullPointerException Se {@code a} è null
	 * @see Action#execute()
	 */
	public void add(Action a, boolean execute) {
		if(a == null) {
			throw new NullPointerException();
		}
		
		// Setto la data
		a.date = new Date();
		
		if(execute) {
			a.execute();
		}
		
		redo.clear();
		
		// Provo a fare il merge delle azioni.
		// Se non è possibile inserisco nello stack
		if(undo.isEmpty()) {
			undo.push(a);
		} else if(undo.peek().tryToMerge(a)) {
			// Aggiorno la data dell'ultimo merge
			undo.peek().dateLastMerge = new Date();
		} else {
			undo.push(a);
		}
	}
	
	/**
	 * Annulla l'ultima azione
	 * @return Azione annullata; null se nessuna
	 */
	public Action undo() {
		if(undo.isEmpty()) {
			return null;
		} else {
			Action a = undo.pop();
			redo.push(a);
			a.undoExecute();
			
			return a;
		}
	}
	
	/**
	 * Riproduce l'ultima azione annullata
	 * @return Azione eseguita; null se nessuna
	 */
	public Action redo() {
		if(redo.isEmpty()) {
			return null;
		} else {
			Action a = redo.pop();
			undo.push(a);
			a.execute();
			
			return a;
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
public static void main(String[] s) {
		
		Co c = new Co();
		ActionManager<Person> a = new ActionManager<>(c);
		
		Person o1 = new Person("pippo", 12);
		Person o2 = new Person("mario", 10);
		c.l.add(o1);
		a.addAction(new AddAction<Person>(c.l.size() - 1, o1));
		c.l.add(o2);
		a.addAction(new AddAction<Person>(c.l.size() - 1, o2));
		try {
			java.util.List<Action<Person>> l = new java.util.ArrayList<>();
			l.add(new ModifyAction<Person>("age", o1, 456));
			l.add(new ModifyAction<Person>("name", o1, "fuffa"));
			
			a.addAction(new MacroAction<Person>(l));
			
			a.undo();
			a.redo();
			a.undo();
			a.undo();
			a.redo();
			a.undo();
			a.undo();
		} catch (NullPointerException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | java.lang.reflect.InvocationTargetException e) {
			assert false;
			e.printStackTrace();
		}
	}
	
	
	
	private static class Co implements Container<Person> {

		java.util.List<Person> l = new java.util.ArrayList<>();
		
		@Override
		public void addAt(int index, Person value) {
			l.add(index, value);
		}

		@Override
		public void removeAt(int index) {
			l.remove(index);
		}
		
	}
	
	
	private static class Person {
		
		String name;
		int age;
		
		public Person(String name, int age) {
			this.name = name;
			this.age = age;
			
		}


		public String getName() {
			return name;
		}


		public int getAge() {
			return age;
		}


		public void setName(String name) {
			this.name = name;
		}


		public void setAge(int age) {
			this.age = age;
		}
		
		
		
	}*/
}
