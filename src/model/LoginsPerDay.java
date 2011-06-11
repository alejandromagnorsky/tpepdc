package model;

import org.joda.time.LocalDate;

public class LoginsPerDay {

	LocalDate date;
	int quantity;

	public LoginsPerDay() {
		this.date = new LocalDate();
		this.quantity = 0;
	}

	public void reset() {
		this.date = new LocalDate();
		this.quantity = 0;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
