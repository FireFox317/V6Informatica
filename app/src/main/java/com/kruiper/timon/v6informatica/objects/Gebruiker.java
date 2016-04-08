package com.kruiper.timon.v6informatica.objects;

/**
 * Created by timon on 14-3-2016.
 */
public class Gebruiker {
	public String naam, gebruikersnaam, wachtwoord, email;
	public int leeftijd, gebrid;

	public Gebruiker(String naam, String gebruikersnaam, String wachtwoord, int leeftijd, String email) {
		this.naam = naam;
		this.gebruikersnaam = gebruikersnaam;
		this.wachtwoord = wachtwoord;
		this.leeftijd = leeftijd;
		this.email = email;
	}

	public Gebruiker(int id, String naam, String gebruikersnaam, String wachtwoord, int leeftijd, String email) {
		this.naam = naam;
		this.gebruikersnaam = gebruikersnaam;
		this.wachtwoord = wachtwoord;
		this.leeftijd = leeftijd;
		this.gebrid = id;
		this.email = email;
	}

	public Gebruiker(String gebruikersnaam, String wachtwoord) {
		this.gebruikersnaam = gebruikersnaam;
		this.wachtwoord = wachtwoord;
		this.leeftijd = -1;
		this.naam = "";
		this.email = "";
	}

}
