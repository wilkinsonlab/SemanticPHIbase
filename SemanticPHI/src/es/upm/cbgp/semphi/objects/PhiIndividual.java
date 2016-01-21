package es.upm.cbgp.semphi.objects;

import com.hp.hpl.jena.ontology.Individual;

public class PhiIndividual {

	private Individual individual;
	private Individual auxiliarIndividual;
	private int hash;
	
	public PhiIndividual(Individual ind, int h) {
		this.individual = ind;
		this.hash = h;
	}

	public Individual getIndividual() {
		return individual;
	}

	public void setIndividual(Individual individual) {
		this.individual = individual;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public void setAuxiliarIndividual(Individual ai) {
		this.auxiliarIndividual = ai;	
	}
	
	public Individual getAuxiliarIndividual() {
		return this.auxiliarIndividual;
	}
	
}
