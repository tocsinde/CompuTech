/*
 *
 *	SWT-Praktikum TU Dresden 2015
 *	Gruppe 32 - Computech
 *
 *	Stephan Fischer
 *  Anna Gromykina
 *  Kevin Horst
 *  Philipp Oehme
 *
 */

package computech.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.hibernate.annotations.Type;


@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {

		@Id @GeneratedValue private long id;

		private String text;
		private int rating;
		private LocalDateTime date;

		@SuppressWarnings("unused")
		private Comment() {}

		public Comment(String text, int rating, LocalDateTime dateTime) {
			this.text = text;
			this.rating = rating;
			this.date = dateTime;
		}

		public String getText() {
			return text;
		}

		public LocalDateTime getDate() {
			return date;
		}

		public int getRating() {
			return rating;
		}

		@Override
		public String toString() {
			return text;
		}
	}
