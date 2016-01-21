package es.upm.cbgp.semphi.ont;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.cbgp.semphi.logic.Constants;
import es.upm.cbgp.semphi.logic.SPARQLQueryEngine;
import es.upm.cbgp.semphi.logic.StaticUtils;
import es.upm.cbgp.semphi.logic.TimeMeasure;
import es.upm.cbgp.semphi.objects.ExternalURI;
import es.upm.cbgp.semphi.objects.LocalURI;
import es.upm.cbgp.semphi.objects.PhiIndividual;
import es.upm.cbgp.semphi.objects.PhiboURI;
import es.upm.cbgp.semphi.objects.Substitution;
import es.upm.cbgp.semphi.phiextractor.PhiEntry;
import es.upm.cbgp.semphi.phiextractor.PhiXMLExtractor;

public class OntologyPopulator {

	private TimeMeasure timeMeasure;

	private LinkedList<PhiEntry> phiEntries;
	private Map<String, String> scientificNames;
	private Map<String, String> mutantPhenotypesOntClasses;
	private Map<String, String> experimentalEvidencesOntClasses;
	private Map<String, String> geneAccessionURIsByKnownDB;
	private Map<String, String> geneAccessionURIsByUnknownDB;
	private Map<String, String> diseasesNamesURIs;
	private Map<String, String> inVitroGrowthsURIs;

	private Map<String, String> mappingsDBClassTypes;

	private Map<String, LinkedList<String>> diseasesNamesSeeAlsos;

	private Map<String, Individual> diseaseNames;
	private Map<String, Individual> descriptionProtocols;
	private Map<String, Individual> pubmedIDs;
	private Map<String, Individual> species;
	private Map<String, Individual> hostResponses;
	private Map<Integer, PhiIndividual> pathogenTaxons;
	private Map<Integer, PhiIndividual> hostTaxons;
	private Map<Integer, PhiIndividual> descriptions;
	private Map<Integer, PhiIndividual> citations;
	private Map<Integer, PhiIndividual> interactionContexts;
	private Map<Integer, PhiIndividual> genes;
	private Map<String, Individual> ntSeqs;
	private Map<String, Individual> aaSeqs;
	private Map<String, Individual> geneNames;
	private Map<String, Individual> geneAccessions;
	private Map<String, Individual> geneFunctions;
	private Map<String, Individual> pathogenAssociatedStrains;
	private Map<String, Individual> pathogenStrains;
	private Map<String, Individual> letalKnockouts;
	private Map<String, Individual> locusIDs;
	private Map<String, Individual> inVitroGrowths;
	private Map<String, Individual> goAnnotations;

	private OntModel model;

	private DatatypeProperty has_value;
	private ObjectProperty citationProperty;
	private ObjectProperty has_quality;
	private ObjectProperty is_described_by;
	private ObjectProperty depends_on;
	private ObjectProperty is_attribute_of;
	private ObjectProperty has_unique_identifier;
	private ObjectProperty is_unique_identifier_for;
	private ObjectProperty is_variant_of;
	private ObjectProperty is_proper_part_of;
	private ObjectProperty is_manifested_as;
	private ObjectProperty is_manifestation_of;
	private ObjectProperty has_part;
	private ObjectProperty has_participant;
	private ObjectProperty has_output;
	private ObjectProperty participates_in;
	private ObjectProperty is_member_of;
	private ObjectProperty has_member;
	private ObjectProperty is_output_of;
	private ObjectProperty affects;
	private ObjectProperty broader;
	private ObjectProperty narrower;

	private Map<String, Integer> idNumericValues;

	private OntClass class_wild_type_genotype;
	private OntClass class_pubmed_identifier;
	private OntClass class_creative_work;
	private OntClass class_description;
	private OntClass class_phenotype_outcome;
	private OntClass class_experiment_specification;
	private OntClass class_protein_sequence;
	private OntClass class_protein_accession;
	private OntClass class_allele;
	private OntClass class_associated_strain;
	private OntClass class_disease_name;
	private OntClass class_disease;
	private OntClass class_experimental_host;
	private OntClass class_function;
	private OntClass class_gene_sio;
	private OntClass class_gene_sofa;
	private OntClass class_gene_lethal_knockout;
	private OntClass class_gene_name;
	private OntClass class_host_context;
	private OntClass class_host_response;
	private OntClass class_ncbi_taxonomy_id;
	private OntClass class_pathogenic_interaction;
	private OntClass class_in_vitro_growth;
	private OntClass class_locus_id;
	private OntClass class_nt_seq;
	private OntClass class_pathogen_sio;
	private OntClass class_pathogen_efo;
	private OntClass class_pathogen_context;
	private OntClass class_scientific_name;
	private OntClass class_strain;
	private OntClass class_phi_base_accession;
	private OntClass class_go_concept_id;
	private OntClass class_attribute;
	private OntClass class_interaction_context;
	private OntClass class_investigation;
	private OntClass class_host_sio;
	private OntClass class_host_efo;
	private OntClass class_organism_obi;
	private OntClass class_organism_sio;

	private PhiXMLExtractor phiXMLExtractor;
	private boolean useSKOSAnnotation;

	/**
	 * Constructor.
	 * 
	 * @param pe
	 *            Phibase entries.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public OntologyPopulator(PhiXMLExtractor pxe, boolean useSkos)
			throws Exception {
		this.useSKOSAnnotation = useSkos;
		this.phiEntries = pxe.getPHIEntries();
		this.phiXMLExtractor = pxe;
		this.diseaseNames = new HashMap<String, Individual>();
		this.descriptionProtocols = new HashMap<String, Individual>();
		this.pubmedIDs = new HashMap<String, Individual>();
		this.species = new HashMap<String, Individual>();
		this.hostResponses = new HashMap<String, Individual>();
		this.pathogenTaxons = new HashMap<Integer, PhiIndividual>();
		this.hostTaxons = new HashMap<Integer, PhiIndividual>();
		this.descriptions = new HashMap<Integer, PhiIndividual>();
		this.citations = new HashMap<Integer, PhiIndividual>();
		this.interactionContexts = new HashMap<Integer, PhiIndividual>();
		this.interactionContexts = new HashMap<Integer, PhiIndividual>();
		this.genes = new HashMap<Integer, PhiIndividual>();
		this.ntSeqs = new HashMap<String, Individual>();
		this.aaSeqs = new HashMap<String, Individual>();
		this.geneNames = new HashMap<String, Individual>();
		this.geneAccessions = new HashMap<String, Individual>();
		this.geneFunctions = new HashMap<String, Individual>();
		this.pathogenAssociatedStrains = new HashMap<String, Individual>();
		this.pathogenStrains = new HashMap<String, Individual>();
		this.letalKnockouts = new HashMap<String, Individual>();
		this.locusIDs = new HashMap<String, Individual>();
		this.inVitroGrowths = new HashMap<String, Individual>();
		this.goAnnotations = new HashMap<String, Individual>();
		this.idNumericValues = new HashMap<String, Integer>();
		this.idNumericValues.put(Constants.INTERACTION, 0);
		this.idNumericValues.put(Constants.PHI_BASE_ID, 0);
		this.idNumericValues.put(Constants.DISEASE_NAME, 0);
		this.idNumericValues.put(Constants.HOST_CONTEXT, 0);
		this.idNumericValues.put(Constants.EXPERIMENTAL_HOST, 0);
		this.idNumericValues.put(Constants.HOST_NCBI_TAXONOMY_ID, 0);
		this.idNumericValues.put(Constants.SPECIES, 0);
		this.idNumericValues.put(Constants.HOST_RESPONSE, 0);
		this.idNumericValues.put(Constants.PATHOGEN_CONTEXT, 0);
		this.idNumericValues.put(Constants.ALLELE, 0);
		this.idNumericValues.put(Constants.GENE, 0);
		this.idNumericValues.put(Constants.NT_SEQUENCE, 0);
		this.idNumericValues.put(Constants.AA_SEQUENCE, 0);
		this.idNumericValues.put(Constants.GENE_NAME, 0);
		this.idNumericValues.put(Constants.GENE_ACCESSION, 0);
		this.idNumericValues.put(Constants.GENE_FUNCTION, 0);
		this.idNumericValues.put(Constants.GENE_GMP, 0);
		this.idNumericValues.put(Constants.EXPERIMENTAL_EVIDENCES, 0);
		this.idNumericValues.put(Constants.MUTATION_PHENOTYPE, 0);
		this.idNumericValues.put(Constants.PATHOGEN, 0);
		this.idNumericValues.put(Constants.PATHOGEN_NCBI_TAXONOMY_ID, 0);
		this.idNumericValues.put(Constants.PATHOGEN_STRAIN, 0);
		this.idNumericValues.put(Constants.PATHOGEN_ASSOCIATED_STRAIN, 0);
		this.idNumericValues.put(Constants.LETHAL_KNOCKOUT, 0);
		this.idNumericValues.put(Constants.LOCUS_ID, 0);
		this.idNumericValues.put(Constants.IN_VITRO_GROWTH, 0);
		this.idNumericValues.put(Constants.GO_ANNOTATION, 0);
		this.idNumericValues.put(Constants.INTERACTION_CONTEXT, 0);
		this.idNumericValues.put(Constants.HOST_WILD_TYPE, 0);
		this.idNumericValues.put(Constants.PROTOCOL, 0);
		this.idNumericValues.put(Constants.DESCRIPTION, 0);
		this.idNumericValues.put(Constants.PATHOGEN_WILD_TYPE, 0);
		this.idNumericValues.put(Constants.DESCRIPTION_PROTOCOL, 0);
		this.idNumericValues.put(Constants.HOST, 0);
		this.idNumericValues.put(Constants.TAXON_ID, 0);
		this.idNumericValues.put(Constants.CITATION, 0);
		this.idNumericValues.put(Constants.PUBMED_ID, 0);
		loadScientificNames();
		loadMutantPhenotypes();
		loadExperimentalEvidences();
		loadGeneAccessionURIs();
		loadDiseasesNamesURIs();
		loadIVGs();
		loadMappingsDBClassType();
		initOntology();
	}

	/**
	 * Method to load the gene accession URIs file.
	 * 
	 * Depending on the value of DB_Type tag, we use for the gene accesion
	 * individual URI a concrete URI. This file contains, for each possible
	 * DB_Type value tag, the URI associated.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadGeneAccessionURIs() throws Exception {
		this.geneAccessionURIsByKnownDB = new HashMap<String, String>();
		this.geneAccessionURIsByUnknownDB = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.GENE_ACCESSIONS_URIS_KNOWN_DBS_FILE
						+ this.phiXMLExtractor.getPHIFile().getVersion()
						+ ".lst"));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String dbtype = parts[0];
				String uri = parts[1];
				this.geneAccessionURIsByKnownDB.put(dbtype, uri);
			} else {
				System.err
						.println("Error reading gene accessions (known dbs) uris file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();
		bL = new BufferedReader(new FileReader(
				Constants.GENE_ACCESSIONS_URIS_UNKNOWN_DBS_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String accession = parts[0].trim();
				String uri = parts[1].trim();
				this.geneAccessionURIsByUnknownDB.put(accession, uri);
			} else {
				System.err
						.println("Error reading gene accessions (unknown dbs) uris file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();

	}

	/**
	 * Method to load the experimental evidences matching file.
	 * 
	 * This file contains the matching between the text contained for some
	 * experimental evidences contained in phi data and the class associated.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadExperimentalEvidences() throws Exception {
		this.experimentalEvidencesOntClasses = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.EXPERIMENTAL_EVIDENCES_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String ee = parts[0];
				String ontClass = parts[1];
				String ontClassFinal = Constants.ONT_URI_BASE_SCHEMA + ontClass;
				this.experimentalEvidencesOntClasses.put(ee, ontClassFinal);
			} else {
				System.err
						.println("Error reading experimental evidences file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();

	}

	/**
	 * Method to load the IVGs mappings.
	 * 
	 * This file contains the mapping between the text contained for some IVGs
	 * in phi data and the individual associated.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadIVGs() throws Exception {
		this.inVitroGrowthsURIs = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.IVG_MAPPINGS_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String ivg = parts[0];
				String ind = parts[1];
				String finalInd = Constants.ONT_URI_BASE_SCHEMA + ind;
				this.inVitroGrowthsURIs.put(ivg, finalInd);
			} else {
				System.err
						.println("Error reading ivgs file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();
	}

	/**
	 * Method to load the class type associated to a given db mapping.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadMappingsDBClassType() throws Exception {
		this.mappingsDBClassTypes = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.MAPPINGS_DBS_CLASS_TYPE_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String db = parts[0];
				String uri = parts[1];
				this.mappingsDBClassTypes.put(db, uri);
			} else {
				System.err
						.println("Error reading mapping db class type file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();
	}

	/**
	 * Method to load the diseases names URIs.
	 * 
	 * This file contains the associated URIs for the diseases names.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadDiseasesNamesURIs() throws Exception {
		this.diseasesNamesURIs = new HashMap<String, String>();
		this.diseasesNamesSeeAlsos = new HashMap<String, LinkedList<String>>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.DISEASES_NAMES_URIS_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 3) {
				String dn = parts[0].trim().toUpperCase();
				String value = parts[1];
				String type = parts[2];
				if (type.equalsIgnoreCase(Constants.URI)) {
					this.diseasesNamesURIs.put(dn, value);
				}
				if (type.equalsIgnoreCase(Constants.SEE_ALSO)) {
					String partsV[] = value.split("@");
					LinkedList<String> values = new LinkedList<String>();
					for (int i = 0; i < partsV.length; i++) {
						values.add(partsV[i]);
					}
					this.diseasesNamesSeeAlsos.put(dn, values);
				}
			} else {
				System.err
						.println("Error reading diseases names uris file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();
	}

	/**
	 * Method to load the mutant phenotypes matching file.
	 * 
	 * This file contains the matching between the text contained for some
	 * mutant phenotypes contained in phi data and the class associated.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadMutantPhenotypes() throws Exception {
		this.mutantPhenotypesOntClasses = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.MUTANT_PHENOTYPES_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String mp = parts[0];
				String ontClass = parts[1];
				String ontClassFinal = Constants.ONT_URI_BASE_SCHEMA + ontClass;
				this.mutantPhenotypesOntClasses.put(mp, ontClassFinal);
			} else {
				System.err
						.println("Error reading mutant phenotypes file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();
	}

	/**
	 * Method to load the scientific names matching file.
	 * 
	 * This file contains the matching between normal species name and the
	 * associated scientific name.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadScientificNames() throws Exception {
		this.scientificNames = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.SCIENTIFIC_NAMES_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String normalName = parts[0].toUpperCase();
				String scientificName = parts[1];
				this.scientificNames.put(normalName, scientificName);
			} else {
				System.err
						.println("Error reading scientific names file. Incorrect format: "
								+ rd + " - " + parts.length);
			}
		}
		bL.close();
	}

	/**
	 * Method to init the ontology.
	 */
	private void initOntology() {
		System.out.println("Initializing ontology..");
		this.model = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		this.model.read(new File(Constants.ONT_SCHEMA_FILE).toURI().toString());
		this.has_value = model
				.getDatatypeProperty(Constants.ONT_PROP_HAS_VALUE_PPIO);
		this.has_value.addLabel(model.createTypedLiteral(this.has_value
				.getLabel(null)));
		this.has_quality = model
				.getObjectProperty(Constants.ONT_PROP_HAS_QUALITY_PPIO);
		this.has_quality.addLabel(model.createTypedLiteral(this.has_quality
				.getLabel(null)));
		this.is_described_by = model
				.getObjectProperty(Constants.ONT_PROP_IS_DESCRIBED_BY_PPIO);
		this.is_described_by.addLabel(model
				.createTypedLiteral(this.is_described_by.getLabel(null)));

		this.citationProperty = model
				.createObjectProperty(Constants.ONT_PROP_CITATION);
		// this.citation.addLabel(model.createTypedLiteral(this.citation
		// .getLabel(null)));
		this.is_attribute_of = model
				.getObjectProperty(Constants.ONT_PROP_IS_ATTRIBUTE_OF_PPIO);
		this.is_attribute_of.addLabel(model
				.createTypedLiteral(this.is_attribute_of.getLabel(null)));

		this.is_manifested_as = model
				.getObjectProperty(Constants.ONT_PROP_IS_MANIFESTED_AS_PPIO);
		this.is_manifested_as.addLabel(model
				.createTypedLiteral(this.is_manifested_as.getLabel(null)));

		this.is_manifestation_of = model
				.getObjectProperty(Constants.ONT_PROP_IS_MANIFESTATION_OF_PPIO);
		this.is_manifestation_of.addLabel(model
				.createTypedLiteral(this.is_manifestation_of.getLabel(null)));

		this.has_unique_identifier = model
				.getObjectProperty(Constants.ONT_PROP_HAS_UNIQUE_IDENTIFIER_PPIO);
		this.has_unique_identifier.addLabel(model
				.createTypedLiteral(this.has_unique_identifier.getLabel(null)));
		this.is_unique_identifier_for = model
				.getObjectProperty(Constants.ONT_PROP_IS_UNIQUE_IDENTIFIER_FOR_PPIO);
		this.is_unique_identifier_for.addLabel(model
				.createTypedLiteral(this.is_unique_identifier_for
						.getLabel(null)));
		this.is_variant_of = model
				.getObjectProperty(Constants.ONT_PROP_IS_VARIANT_OF_PPIO);
		this.is_variant_of.addLabel(model.createTypedLiteral(this.is_variant_of
				.getLabel(null)));
		this.depends_on = model
				.getObjectProperty(Constants.ONT_PROP_DEPENDS_ON_PPIO);
		this.depends_on.addLabel(model.createTypedLiteral(this.depends_on
				.getLabel(null)));
		this.is_proper_part_of = model
				.getObjectProperty(Constants.ONT_PROP_IS_PROPER_PART_OF_PPIO);
		this.is_proper_part_of.addLabel(model
				.createTypedLiteral(this.is_proper_part_of.getLabel(null)));
		this.has_part = model
				.getObjectProperty(Constants.ONT_PROP_HAS_PART_PPIO);
		this.has_part.addLabel(model.createTypedLiteral(this.has_part
				.getLabel(null)));
		this.has_participant = model
				.getObjectProperty(Constants.ONT_PROP_HAS_PARTICIPANT_PPIO);
		this.has_participant.addLabel(model
				.createTypedLiteral(this.has_participant.getLabel(null)));
		this.has_output = model
				.getObjectProperty(Constants.ONT_PROP_HAS_OUTPUT_PPIO);
		this.has_output.addLabel(model.createTypedLiteral(this.has_output
				.getLabel(null)));
		this.participates_in = model
				.getObjectProperty(Constants.ONT_PROP_PARTICIPATES_IN_PPIO);
		this.participates_in.addLabel(model
				.createTypedLiteral(this.participates_in.getLabel(null)));
		this.is_member_of = model
				.getObjectProperty(Constants.ONT_PROP_IS_MEMBER_OF_PPIO);
		this.is_member_of.addLabel(model.createTypedLiteral(this.is_member_of
				.getLabel(null)));
		this.has_member = model
				.getObjectProperty(Constants.ONT_PROP_HAS_MEMBER_PPIO);
		this.has_member.addLabel(model.createTypedLiteral(this.has_member
				.getLabel(null)));

		this.is_output_of = model
				.getObjectProperty(Constants.ONT_PROP_IS_OUTPUT_OF_PPIO);
		this.is_output_of.addLabel(model.createTypedLiteral(this.is_output_of
				.getLabel(null)));

		this.affects = model.getObjectProperty(Constants.ONT_PROP_AFFECTS_PPIO);
		this.affects.addLabel(model.createTypedLiteral(this.affects
				.getLabel(null)));
		this.broader = model.getObjectProperty(Constants.ONT_PROP_BROADER);
		this.narrower = model.getObjectProperty(Constants.ONT_PROP_NARROWER);
		this.class_protein_sequence = model
				.getOntClass(Constants.ONT_CLASS_PROTEIN_SEQUENCE);
		this.class_protein_sequence
				.addLabel(model.createTypedLiteral(this.class_protein_sequence
						.getLabel(null)));
		this.class_protein_accession = model
				.getOntClass(Constants.ONT_CLASS_PROTEIN_ACCESSION);
		this.class_protein_accession
				.addLabel(model.createTypedLiteral(this.class_protein_accession
						.getLabel(null)));
		this.class_allele = model.getOntClass(Constants.ONT_CLASS_ALLELE);
		this.class_allele.addLabel(model.createTypedLiteral(this.class_allele
				.getLabel(null)));
		this.class_associated_strain = model
				.getOntClass(Constants.ONT_CLASS_ASSOCIATED_STRAIN);
		this.class_associated_strain
				.addLabel(model.createTypedLiteral(this.class_associated_strain
						.getLabel(null)));
		this.class_disease = model.getOntClass(Constants.ONT_CLASS_DISEASE);
		this.class_disease.addLabel(model.createTypedLiteral(this.class_disease
				.getLabel(null)));
		this.class_disease_name = model
				.getOntClass(Constants.ONT_CLASS_DISEASE_NAME);
		this.class_disease_name.addLabel(model
				.createTypedLiteral(this.class_disease_name.getLabel(null)));
		this.class_wild_type_genotype = model
				.getOntClass(Constants.ONT_CLASS_WILD_TYPE_GENOTYPE);
		this.class_wild_type_genotype.addLabel(model
				.createTypedLiteral(this.class_wild_type_genotype
						.getLabel(null)));
		this.class_pubmed_identifier = model
				.getOntClass(Constants.ONT_CLASS_PUBMED_IDENTIFIER);
		this.class_pubmed_identifier
				.addLabel(model.createTypedLiteral(this.class_pubmed_identifier
						.getLabel(null)));

		this.class_creative_work = model
				.createClass(Constants.ONT_CLASS_CREATIVE_WORK);
		// this.class_creative_work.addLabel(model
		// .createTypedLiteral(this.class_creative_work.getLabel(null)));

		this.class_description = model
				.getOntClass(Constants.ONT_CLASS_DESCRIPTION);
		this.class_description.addLabel(model
				.createTypedLiteral(this.class_description.getLabel(null)));
		this.class_phenotype_outcome = model
				.getOntClass(Constants.ONT_CLASS_POO);
		this.class_phenotype_outcome
				.addLabel(model.createTypedLiteral(this.class_phenotype_outcome
						.getLabel(null)));
		this.class_experiment_specification = model
				.getOntClass(Constants.ONT_CLASS_ESO);
		this.class_experiment_specification.addLabel(model
				.createTypedLiteral(this.class_experiment_specification
						.getLabel(null)));
		this.class_experimental_host = model
				.getOntClass(Constants.ONT_CLASS_EXPERIMENTAL_HOST);
		this.class_experimental_host
				.addLabel(model.createTypedLiteral(this.class_experimental_host
						.getLabel(null)));
		this.class_function = model.getOntClass(Constants.ONT_CLASS_FUNCTION);
		this.class_function.addLabel(model
				.createTypedLiteral(this.class_function.getLabel(null)));
		this.class_gene_sio = model.getOntClass(Constants.ONT_CLASS_GENE_SIO);
		this.class_gene_sio.addLabel(model
				.createTypedLiteral(this.class_gene_sio.getLabel(null)));

		this.class_host_efo = model.getOntClass(Constants.ONT_CLASS_HOST_EFO);
		this.class_host_efo.addLabel(model
				.createTypedLiteral(this.class_host_efo.getLabel(null)));

		this.class_organism_obi = model
				.getOntClass(Constants.ONT_CLASS_ORGANISM_OBI);
		this.class_organism_obi.addLabel(model
				.createTypedLiteral(this.class_organism_obi.getLabel(null)));

		this.class_organism_sio = model
				.getOntClass(Constants.ONT_CLASS_ORGANISM_SIO);
		this.class_organism_sio.addLabel(model
				.createTypedLiteral(this.class_organism_sio.getLabel(null)));

		this.class_host_sio = model.getOntClass(Constants.ONT_CLASS_HOST_SIO);
		this.class_host_sio.addLabel(model
				.createTypedLiteral(this.class_host_sio.getLabel(null)));

		this.class_gene_sofa = model.getOntClass(Constants.ONT_CLASS_GENE_SOFA);
		this.class_gene_sofa.addLabel(model
				.createTypedLiteral(this.class_gene_sofa.getLabel(null)));

		this.class_investigation = model
				.getOntClass(Constants.ONT_CLASS_INVESTIGATION);
		this.class_investigation.addLabel(model
				.createTypedLiteral(this.class_investigation.getLabel(null)));
		this.class_gene_lethal_knockout = model
				.getOntClass(Constants.ONT_CLASS_GENE_LETHAL_KNOCKOUT);
		this.class_gene_name = model.getOntClass(Constants.ONT_CLASS_GENE_NAME);
		this.class_host_context = model
				.getOntClass(Constants.ONT_CLASS_HOST_CONTEXT);
		this.class_interaction_context = model
				.getOntClass(Constants.ONT_CLASS_INTERACTION_CONTEXT);
		this.class_host_response = model
				.getOntClass(Constants.ONT_CLASS_HOST_RESPONSE);
		this.class_ncbi_taxonomy_id = model
				.getOntClass(Constants.ONT_CLASS_NCBI_TAXONOMY_ID);
		this.class_pathogenic_interaction = model
				.getOntClass(Constants.ONT_CLASS_PATHOGENIC_INTERACTION);
		this.class_in_vitro_growth = model
				.getOntClass(Constants.ONT_CLASS_IN_VITRO_GROWTH);
		this.class_locus_id = model.getOntClass(Constants.ONT_CLASS_LOCUS_ID);
		this.class_nt_seq = model.getOntClass(Constants.ONT_CLASS_NT_SEQ);
		this.class_pathogen_sio = model
				.getOntClass(Constants.ONT_CLASS_PATHOGEN_SIO);
		this.class_pathogen_sio.addLabel(model
				.createTypedLiteral(this.class_pathogen_sio.getLabel(null)));
		this.class_pathogen_efo = model
				.getOntClass(Constants.ONT_CLASS_PATHOGEN_EFO);
		this.class_pathogen_efo.addLabel(model
				.createTypedLiteral(this.class_pathogen_efo.getLabel(null)));
		this.class_pathogen_context = model
				.getOntClass(Constants.ONT_CLASS_PATHOGEN_CONTEXT);
		this.class_pathogen_context
				.addLabel(model.createTypedLiteral(this.class_pathogen_context
						.getLabel(null)));
		this.class_scientific_name = model
				.getOntClass(Constants.ONT_CLASS_SCIENTIFIC_NAME);
		this.class_scientific_name.addLabel(model
				.createTypedLiteral(this.class_scientific_name.getLabel(null)));
		this.class_strain = model.getOntClass(Constants.ONT_CLASS_STRAIN);
		this.class_strain.addLabel(model.createTypedLiteral(this.class_strain
				.getLabel(null)));
		this.class_phi_base_accession = model
				.getOntClass(Constants.ONT_CLASS_PHI_BASE_ACCESSION);
		this.class_phi_base_accession.addLabel(model
				.createTypedLiteral(this.class_phi_base_accession
						.getLabel(null)));
		this.class_go_concept_id = model
				.getOntClass(Constants.ONT_CLASS_GO_CONCEPT_ID);
		this.class_go_concept_id.addLabel(model
				.createTypedLiteral(this.class_go_concept_id.getLabel(null)));
		this.class_attribute = model.getOntClass(Constants.ONT_CLASS_ATTRIBUTE);
		this.class_attribute.addLabel(model
				.createTypedLiteral(this.class_attribute.getLabel(null)));

	}

	/**
	 * Method to execute the ontology population task.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void run() throws Exception {
		boolean enableTM = false;
		timeMeasure = new TimeMeasure(enableTM);
		// this.phiEntries.size()
		//
		long tS = System.currentTimeMillis();
		for (int i = 0; i < this.phiEntries.size(); i++) {
			System.out.println("Creating interaction " + (i + 1) + " of "
					+ this.phiEntries.size());
			createInteraction(this.phiEntries.get(i));
			if (i % 500 == 0 && i > 10) {
				System.gc();
				timeMeasure.saveResults();
			}
		}
		if (useSKOSAnnotation) {
			annotateWithSKOSESOAndPOO();
		}
		System.out.println("Triples: " + this.model.getGraph().size());
		System.out.println("Writing to file..");
		this.model.write(new FileOutputStream(Constants.ONT_SAVE_FILE),
				"TURTLE");

		long tE = System.currentTimeMillis();
		long tt = tE - tS;
		String res = String.format(
				"%02d min, %02d sec",
				TimeUnit.MILLISECONDS.toMinutes(tt),
				TimeUnit.MILLISECONDS.toSeconds(tt)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(tt)));
		System.out.println("Total time: " + res);
	}

	/**
	 * Once the ontology has been fulfilled and we have all the data we need to
	 * annotate the ESO and POO terms using SKOS.
	 */
	private void annotateWithSKOSESOAndPOO() {
		System.out.println("Annotating with SKOS ESO and POO vocabularies..");
		annotateWithSKOS(class_experiment_specification);
		annotateWithSKOS(class_phenotype_outcome);
	}

	/**
	 * Method to annotate with SKOS a given class.
	 * 
	 * @param cl
	 *            Receives the class.
	 */
	private void annotateWithSKOS(OntClass cl) {
		ArrayList<OntResource[]> mods = new ArrayList<OntResource[]>();
		ExtendedIterator<OntClass> subClasses = cl.listSubClasses();
		while (subClasses.hasNext()) {
			OntClass childClass = subClasses.next();
			ArrayList<OntResource[]> res = annotateWithSKOS(cl, childClass);
			mods.addAll(res);
		}
		for (int i = 0; i < mods.size(); i++) {
			OntResource childInstance = mods.get(i)[0];
			OntResource parentInstance = mods.get(i)[1];
			childInstance.addProperty(this.broader, parentInstance);
			parentInstance.addProperty(this.narrower, childInstance);
		}
	}

	/**
	 * Method to annotate with skos based on class-subclass hierarchy to
	 * annotate broader/narrower concepts.
	 * 
	 * @param parentClass
	 *            Receives the parent class.
	 * @param childClass
	 *            Receives the child class.
	 * @return Return the pair of individuals to annotate.
	 */
	private ArrayList<OntResource[]> annotateWithSKOS(OntClass parentClass,
			OntClass childClass) {
		/*
		 * In this arraylist we will save the pairs of individuals that are
		 * going to be annotated.
		 */
		ArrayList<OntResource[]> mods = new ArrayList<OntResource[]>();
		/*
		 * If the child class doesn't have more subclasses.. we go ahead.
		 */
		if (!hasSubClass(childClass)) {
			ArrayList<? extends OntResource> instancesParentClass = (ArrayList<? extends OntResource>) parentClass
					.listInstances(true).toList();
			ArrayList<? extends OntResource> instancesChildClass = (ArrayList<? extends OntResource>) childClass
					.listInstances(true).toList();
			for (int i = 0; i < instancesParentClass.size(); i++) {
				OntResource parentInstance = instancesParentClass.get(i);
				for (int j = 0; j < instancesChildClass.size(); j++) {
					OntResource childInstance = instancesChildClass.get(j);
					if (childInstance != parentInstance) {
						mods.add(new OntResource[] { childInstance,
								parentInstance });
					}
				}
			}
			return mods;
		} else {
			/*
			 * If it has more subclasses.. we call the method recursively.
			 */
			ExtendedIterator<OntClass> subClasses = childClass.listSubClasses();
			while (subClasses.hasNext()) {
				OntClass childClassNew = subClasses.next();
				ArrayList<OntResource[]> res = annotateWithSKOS(childClass,
						childClassNew);
				mods.addAll(res);
			}
			return mods;
		}
	}

	/**
	 * Method to know if a given class has subclasses.
	 * 
	 * @param cl
	 *            Receives the class.
	 * @return Return a boolean.
	 */
	private boolean hasSubClass(OntClass cl) {
		return cl.listSubClasses().toList().size() >= 1;
	}

	/**
	 * Creates the interaction individual.
	 * 
	 * @param pe
	 *            Receives the phientry object.
	 * @throws It
	 *             can throws exception.
	 */
	private void createInteraction(PhiEntry pe) throws Exception {
		long tS = System.currentTimeMillis();
		String interNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.INTERACTION));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_INTERACTION
				+ Constants.PREFIX_INTERACTION + interNum;
		Individual interaction = this.model.createIndividual(uri,
				this.class_pathogenic_interaction);
		String disName = pe.getDiseaseName();
		if (StaticUtils.isEmpty(disName)) {
			disName = "Unknown";
		}
		String expHost = pe.getExperimentalHost();
		if (StaticUtils.isEmpty(expHost)) {
			expHost = "Unknown";
		}
		String patSpe = pe.getPathogenSpecies();
		if (StaticUtils.isEmpty(patSpe)) {
			patSpe = "Unknown";
		}
		String gn = pe.getGeneName();
		if (StaticUtils.isEmpty(gn)) {
			gn = "Unknown";
		}
		interaction.setLabel("Interaction - " + patSpe + " / " + expHost + " ["
				+ disName + "] - " + gn, "en");

		long tSpiu = System.currentTimeMillis();
		Individual phiUniqueIdentifier = createPHIUniqueIdentifier(pe);
		interaction
				.addProperty(this.has_unique_identifier, phiUniqueIdentifier);
		phiUniqueIdentifier.addProperty(this.is_unique_identifier_for,
				interaction);

		timeMeasure.calculateTime(tSpiu,
				"\tTotal time [createPHIUniqueIdentifier]: ");

		long tSpat = System.currentTimeMillis();
		PhiIndividual pathogen = createPathogen(pe);
		Individual pathogenInd = pathogen.getIndividual();
		timeMeasure.calculateTime(tSpat, "\tTotal time [createPathogen]: ");

		Individual pathOrganismInd = pathogen.getAuxiliarIndividual();

		long tSintContWT = System.currentTimeMillis();
		PhiIndividual intContextWT = createInteractionContext(pe, true,
				pathOrganismInd);
		Individual intContextWTInd = intContextWT.getIndividual();

		interaction.addProperty(this.is_manifested_as, intContextWTInd);
		intContextWTInd.addProperty(this.is_manifestation_of, interaction);
		timeMeasure.calculateTime(tSintContWT,
				"\tTotal time [createInteractionContext(WT)]: ");

		long tSintCon = System.currentTimeMillis();
		PhiIndividual intContext = createInteractionContext(pe, false,
				pathOrganismInd);
		Individual intContextInd = intContext.getIndividual();

		interaction.addProperty(this.has_participant, pathogenInd);
		pathogenInd.addProperty(this.participates_in, interaction);

		interaction.addProperty(this.is_manifested_as, intContextInd);
		intContextInd.addProperty(this.is_manifestation_of, interaction);
		timeMeasure.calculateTime(tSintCon,
				"\tTotal time [createInteractionContext]: ");

		long tShost = System.currentTimeMillis();

		PhiIndividual host = createHost(pe);
		Individual hostInd = host.getIndividual();

		interaction.addProperty(this.has_participant, hostInd);
		hostInd.addProperty(this.participates_in, interaction);
		timeMeasure.calculateTime(tShost, "\tTotal time [createHost]: ");

		timeMeasure.calculateTime(tS, "Total time [createInteraction]: ");
	}

	private void enrichWithSeeAlso(Individual ind, String key,
			Map<String, LinkedList<String>> map) {
		LinkedList<String> lst = map.get(key);
		if (lst != null) {
			for (int i = 0; i < lst.size(); i++) {
				String url = lst.get(i);
				if (url != null) {
					ind.addProperty(RDFS.seeAlso, url);
				}
			}
		}
	}

	/**
	 * Method to get the URI associated to a disease name.
	 * 
	 * @param dn
	 *            Receives the disease name.
	 * @return Returns the URI.
	 */
	private PhiboURI getDiseaseNameURI(String dn) {
		if (dn != null) {
			String uri = this.diseasesNamesURIs.get(dn.toUpperCase().trim());
			if (uri != null) {
				return new ExternalURI(uri);
			}
		}
		return new LocalURI(Constants.URI_DISEASE_NAME);
	}

	/**
	 * Method to get the URI associated to a IVG.
	 * 
	 * @param ivg
	 *            Receives the IVG.
	 * @return Returns the value.
	 */
	private PhiboURI getInvitroGrowthURI(String ivg) {
		if (ivg != null) {
			ivg = ivg.toUpperCase().trim();
			String uri = this.inVitroGrowthsURIs.get(ivg);
			if (uri != null) {
				return new ExternalURI(uri);
			}
		}
		return new LocalURI(Constants.URI_IN_VITRO_GROWTH);
	}

	/**
	 * Creates the host context individual.
	 * 
	 * @param pe
	 *            Receives the phientry object.
	 * @param wt
	 *            Receives if it is the wild type host context.
	 * @return The individual.
	 */
	private PhiIndividual createHostContext(PhiEntry pe, boolean wt) {
		Individual hostContext = null;

		int hostContextHash = 0;

		String hostContextNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.HOST_CONTEXT));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_HOST_CONTEXT
				+ Constants.PREFIX_HOST_CONTEXT + hostContextNum;
		hostContext = this.model.createIndividual(uri, this.class_host_context);
		hostContext.setLabel("Host context - " + Constants.PREFIX_HOST_CONTEXT
				+ hostContextNum, "en");
		if (wt) {
			long tShcwt = System.currentTimeMillis();
			// String wtHostNum = String.format(Constants.ZERO_FILL_FORMAT,
			// getIDNumber(Constants.HOST_WILD_TYPE));
			Individual wtHost = createFullIndividual(new LocalURI(
					Constants.URI_HOST_WILD_TYPE),
					Constants.PREFIX_HOST_WILD_TYPE,
					new OntClass[] { this.class_wild_type_genotype },
					Constants.HOST_WILD_TYPE);
			wtHost.setLabel(Constants.WILD_TYPE_LABEL, "en");
			hostContext.addProperty(this.has_quality, wtHost);
			wtHost.addProperty(this.is_attribute_of, hostContext);

			hostContextHash += wtHost.hashCode();

			timeMeasure
					.calculateTime(tShcwt,
							"\t\t\tTotal time [createInteractionContext->hostContext->]: ");
		}
		PhiIndividual hostContextRet = new PhiIndividual(hostContext,
				hostContextHash);
		return hostContextRet;

		// PhiIndividual hostContext_Existing = this.hostContexts
		// .get(hostContextHash);
		// if (hostContext_Existing != null) {
		// return hostContext_Existing;
		// } else {
		// PhiIndividual hostContextRet = new PhiIndividual(hostContext,
		// hostContextHash);
		// this.hostContexts.put(hostContextHash, hostContextRet);
		// return hostContextRet;
		// }

	}

	/**
	 * Method to create the protocol node of an interaction context node.
	 * 
	 * @param pe
	 *            Receives the PHIEntry.
	 * @param wt
	 *            Receives if is for wild type interaction or not.
	 * @return Return the individual
	 */
	private PhiIndividual createProtocol(PhiEntry pe, boolean wt) {
		Individual protocol = null;

		int protocolHash = 0;

		String protocolNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.PROTOCOL));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_PROTOCOL
				+ Constants.PREFIX_PROTOCOL + protocolNum;
		protocol = this.model.createIndividual(uri, this.class_investigation);
		protocol.addOntClass(this.class_experiment_specification);

		LinkedList<String> descriptionProtocolsValues = new LinkedList<String>();
		if (wt) {
			protocol.addOntClass(getClassForExperimentalEvidence(Constants.HISTORICAL_OBSERVATION));
			descriptionProtocolsValues.add(Constants.HISTORICAL_OBSERVATION);
			protocol.setLabel("Protocol - Historical observation", "en");
			// protocol.addProperty(this.has_value,
			// Constants.HISTORICAL_OBSERVATION);
		} else {
			for (int i = 0; i < pe.getExperimentalEvidences().size(); i++) {
				String prot = pe.getExperimentalEvidences().get(i);
				descriptionProtocolsValues.add(prot);
				OntClass specificClass = getClassForExperimentalEvidence(prot);
				if (specificClass != null) {
					protocol.addOntClass(specificClass);
				}
			}
			protocol.setLabel(
					"Protocol - "
							+ StaticUtils.getStringFromList(
									descriptionProtocolsValues, ", "), "en");

			for (int i = 0; i < pe.getProvenanceData().getLiteratureIDs()
					.size(); i++) {
				String litID = pe.getProvenanceData().getLiteratureIDs().get(i);
				if (!StaticUtils.isEmpty(litID)) {
					PhiIndividual citation = createCitation(litID);
					protocolHash += citation.getHash();
					protocol.addProperty(this.citationProperty,
							citation.getIndividual());
				}
			}
		}
		protocolHash += protocol.hashCode();

		Individual descProtocol = createFullIndividualMultipleValues(
				this.descriptionProtocols,
				descriptionProtocolsValues,
				new LocalURI(Constants.URI_PROTOCOL_DESCRIPTION),
				Constants.PREFIX_PROTOCOL_DESCRIPTION,
				new OntClass[] { this.class_description },
				Constants.DESCRIPTION_PROTOCOL,
				"Description Protocol - "
						+ StaticUtils.getStringFromList(
								descriptionProtocolsValues, ", "), null);

		if (descProtocol != null) {
			protocol.addProperty(this.has_quality, descProtocol);
			// no inverse
		}
		protocolHash += descProtocol.hashCode();

		PhiIndividual protocolRet = new PhiIndividual(protocol, protocolHash);
		return protocolRet;

		// PhiIndividual protocol_Existing = this.protocols.get(protocolHash);
		// if (protocol_Existing != null) {
		// return protocol_Existing;
		// } else {
		// PhiIndividual protocolRet = new PhiIndividual(protocol,
		// protocolHash);
		// this.protocols.put(protocolHash, protocolRet);
		// return protocolRet;
		// }
	}

	private PhiIndividual createCitation(String litID) {
		Individual citation = null;

		int citationHash = 0;

		String citationNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.CITATION));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_CITATION
				+ Constants.PREFIX_CITATION + citationNum;
		citation = this.model.createIndividual(uri, this.class_creative_work);
		citation.setLabel("Citation - " + Constants.PREFIX_CITATION
				+ citationNum, "en");

		int intLitID = StaticUtils.getIntFromString(litID);
		if (intLitID != 0) {
			Individual idPubmed = createFullIndividual(pubmedIDs,
					getPubMedIDURI(intLitID, Constants.URI_PUBMED_ID),
					Constants.PREFIX_PUBMED_ID,
					new OntClass[] { this.class_pubmed_identifier },
					Constants.PUBMED_ID, "PMID:" + litID);
			idPubmed.setLabel("PubMed ID: " + litID, "en");
			citation.addProperty(this.has_unique_identifier, idPubmed);
			idPubmed.addProperty(this.is_unique_identifier_for, citation);
		}
		citationHash += citation.hashCode();

		PhiIndividual citation_Existing = this.citations.get(citationHash);
		if (citation_Existing != null) {
			return citation_Existing;
		} else {
			PhiIndividual citationRet = new PhiIndividual(citation,
					citationHash);
			this.citations.put(citationHash, citationRet);
			return citationRet;
		}
	}

	/**
	 * Method to create the description node of an interaction context node.
	 * 
	 * @param pe
	 *            Receives the PHIEntry.
	 * @param wt
	 *            Receives if is for wild type interaction or not.
	 * @return Return the individual
	 */
	private PhiIndividual createDescription(PhiEntry pe, boolean wt) {
		Individual description = null;

		int descriptionHash = 0;

		String descriptionNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.DESCRIPTION));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_DESCRIPTION
				+ Constants.PREFIX_DESCRIPTION + descriptionNum;
		description = this.model.createIndividual(uri, this.class_description);
		description.addOntClass(this.class_phenotype_outcome);
		description.setLabel("Description - " + Constants.PREFIX_DESCRIPTION
				+ descriptionNum, "en");

		if (wt) {
			description
					.addOntClass(getClassForMutationPhenotype(Constants.MUTANT_PHENOTYPE_BASE_STATE));
			description.addProperty(this.has_value,
					Constants.MUTANT_PHENOTYPE_BASE_STATE, "en");
		} else {
			if (pe.getPhenotypeOfMutant() != null) {
				OntClass specificClass = getClassForMutationPhenotype(pe
						.getPhenotypeOfMutant());
				if (specificClass != null) {
					description.addOntClass(specificClass);
				}

				description.addProperty(this.has_value,
						pe.getPhenotypeOfMutant(), "en");
			}
		}

		descriptionHash += description.hashCode();

		Individual diseaseName = createFullIndividual(this.diseaseNames,
				pe.getDiseaseName(), getDiseaseNameURI(pe.getDiseaseName()),
				Constants.PREFIX_DISEASE_NAME, new OntClass[] {
						this.class_disease_name, this.class_disease },
				Constants.DISEASE_NAME, "Disease name - " + pe.getDiseaseName());

		if (diseaseName != null) {
			description.addProperty(this.is_described_by, diseaseName);
			// no inverse
			enrichWithSeeAlso(diseaseName, pe.getDiseaseName(),
					this.diseasesNamesSeeAlsos);
		}
		descriptionHash += diseaseName.hashCode();

		PhiIndividual description_Existing = this.descriptions
				.get(descriptionHash);
		if (description_Existing != null) {
			return description_Existing;
		} else {
			PhiIndividual descriptionRet = new PhiIndividual(description,
					descriptionHash);
			this.descriptions.put(descriptionHash, descriptionRet);
			return descriptionRet;
		}
	}

	/**
	 * Creates the pathogen context individual.
	 * 
	 * @param pe
	 *            Receives the phientry object.
	 * @param wt
	 *            Receives if it is the wild type pathogen context.
	 * @return The individual.
	 */
	private PhiIndividual createPathogenContext(PhiEntry pe, boolean wt,
			Individual pathogen) {
		Individual pathogenContext = null;

		int pathogenContextHash = 0;

		String pathogenContextNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.PATHOGEN_CONTEXT));
		String uri = Constants.ONT_URI_BASE_DATA
				+ Constants.URI_PATHOGEN_CONTEXT
				+ Constants.PREFIX_PATHOGEN_CONTEXT + pathogenContextNum;
		pathogenContext = this.model.createIndividual(uri,
				this.class_pathogen_context);
		pathogenContext.setLabel("Pathogen context - "
				+ Constants.PREFIX_PATHOGEN_CONTEXT + pathogenContextNum, "en");

		/*
		 * If we are creating the pathogen context of the wild type, things
		 * become easier: pathogen context only makes reference to the wild
		 * type.
		 * 
		 * If not, we have to go through the allele.
		 */
		if (wt) {
			// String wtPathogenNum = String.format(Constants.ZERO_FILL_FORMAT,
			// getIDNumber(Constants.PATHOGEN_WILD_TYPE));
			long tSwtp = System.currentTimeMillis();
			Individual wtPath = createFullIndividual(new LocalURI(
					Constants.URI_PATHOGEN_WILD_TYPE),
					Constants.PREFIX_PATHOGEN_WILD_TYPE,
					new OntClass[] { this.class_wild_type_genotype },
					Constants.PATHOGEN_WILD_TYPE);
			pathogenContext.addProperty(this.has_quality, wtPath);
			wtPath.addProperty(this.is_attribute_of, pathogenContext);

			pathogenContextHash += wtPath.hashCode();
			timeMeasure
					.calculateTime(tSwtp,
							"\t\t\tTotal time [createInteractionContext->pathogenContext->wtPathogen]: ");
		} else {
			long tSallele = System.currentTimeMillis();
			PhiIndividual allele = createAllele(pe, pathogen);
			Individual alleleInd = allele.getIndividual();

			pathogenContextHash += allele.getHash();

			pathogenContext.addProperty(this.has_quality, alleleInd);
			alleleInd.addProperty(this.is_attribute_of, pathogenContext);
			timeMeasure
					.calculateTime(tSallele,
							"\t\t\tTotal time [createInteractionContext->pathogenContext->allele]: ");
		}
		PhiIndividual pathogenContextRet = new PhiIndividual(pathogenContext,
				pathogenContextHash);
		return pathogenContextRet;
		// PhiIndividual pathogenContext_Existing = this.pathogenContexts
		// .get(pathogenContextHash);
		// if (pathogenContext_Existing != null) {
		// return pathogenContext_Existing;
		// } else {
		// PhiIndividual pathogenContextRet = new PhiIndividual(pathogenContext,
		// pathogenContextHash);
		// this.pathogenContexts.put(pathogenContextHash, pathogenContextRet);
		// return pathogenContextRet;
		// }
	}

	/**
	 * Creates the allele individual.
	 * 
	 * @param pe
	 *            Receives the entry.
	 * @return The individual.
	 */
	private PhiIndividual createAllele(PhiEntry pe, Individual pathogen) {
		// System.out.println("\t\tCreating allele..");
		Individual allele = null;
		int alleleHash = 0;

		String alleleNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.ALLELE));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_ALLELE
				+ Constants.PREFIX_ALLELE + alleleNum;
		allele = this.model.createIndividual(uri, this.class_allele);
		allele.setLabel("Allele - " + Constants.PREFIX_ALLELE + alleleNum, "en");

		long tSivg = System.currentTimeMillis();
		if (pe.getInVitroGrowth() != null) {
			Individual ivg = createFullIndividual(this.inVitroGrowths,
					pe.getInVitroGrowth(),
					getInvitroGrowthURI(pe.getInVitroGrowth()),
					Constants.PREFIX_IN_VITRO_GROWTH,
					new OntClass[] { this.class_in_vitro_growth },
					Constants.IN_VITRO_GROWTH,
					"In vitro growth - " + pe.getInVitroGrowth());

			alleleHash += getHash(pe.getInVitroGrowth());

			if (ivg != null) {
				allele.addProperty(this.has_quality, ivg);
				ivg.addProperty(this.is_attribute_of, allele);
			}
		}
		timeMeasure
				.calculateTime(tSivg,
						"\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->ivg]: ");
		long tSlk = System.currentTimeMillis();
		if (pe.getLethalKnockout() != null) {
			Individual letalKnockout = createFullIndividual(
					this.letalKnockouts, pe.getLethalKnockout(), new LocalURI(
							Constants.URI_LETHAL_KNOCKOUT),
					Constants.PREFIX_LETHAL_KNOCKOUT,
					new OntClass[] { this.class_gene_lethal_knockout },
					Constants.LETHAL_KNOCKOUT,
					"Lethal Knockout - " + pe.getLethalKnockout());

			alleleHash += getHash(pe.getLethalKnockout());

			if (letalKnockout != null) {
				allele.addProperty(this.has_quality, letalKnockout);
				letalKnockout.addProperty(this.is_attribute_of, allele);
			}
		}
		timeMeasure
				.calculateTime(
						tSlk,
						"\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->lethal knockout]: ");
		long tSgene = System.currentTimeMillis();

		PhiIndividual gene = createGene(pe, pathogen);
		Individual geneInd = gene.getIndividual();
		allele.addProperty(this.is_variant_of, geneInd);

		alleleHash += gene.getHash();
		timeMeasure
				.calculateTime(
						tSgene,
						"\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene]: ");

		PhiIndividual alleleRet = new PhiIndividual(allele, alleleHash);
		// this.alleles.put(alleleHash, alleleRet);
		return alleleRet;

		// PhiIndividual allele_Existing = this.alleles.get(alleleHash);
		// if (allele_Existing != null) {
		// return allele_Existing;
		// } else {
		// PhiIndividual alleleRet = new PhiIndividual(allele, alleleHash);
		// this.alleles.put(alleleHash, alleleRet);
		// return alleleRet;
		// }
	}

	/**
	 * Method to create a full individual with all the parameters except
	 * sameAsMappings
	 * 
	 * @param map
	 *            Map where are stored previous individuals that could be the
	 *            same. We get the individual from this map (based on the
	 *            value), and if exists, we return it. If not, we create it with
	 *            the associated value.
	 * @param value
	 *            Value to be inserted (trough 'has value' data property).
	 * @param URI
	 *            URI of the individual to be used.
	 * @param prefix
	 *            Prefix to be used as individual name.
	 * @param ontClasses
	 *            Classes where the individual belongs to.
	 * @param idValue
	 *            ID of the individual name (PREFIX + idValue is the invidual
	 *            name)
	 * @param label
	 *            Label to be attached to the individual.
	 * @return Returns the individual.
	 */
	private Individual createFullIndividual(Map<String, Individual> map,
			String value, PhiboURI URI, String prefix, OntClass[] ontClasses,
			String idValue, String label) {
		return createFullIndividual(map, value, URI, prefix, ontClasses,
				idValue, label, null);

	}

	/**
	 * Method to create full individual with all the parameters except
	 * sameAsMappings, value and map.
	 * 
	 * The idea is that we should ALWAYS create different individuals if we call
	 * this method. This is the reason because we don't check in the map if we
	 * already have this individual created. It is also an "empty" individual in
	 * the sense that it does not has any value (no 'has value'). It neither has
	 * sameAsMappings.
	 * 
	 * @param URI
	 *            URI of the individual to be used.
	 * @param prefix
	 *            Prefix to be used as individual name.
	 * @param ontClasses
	 *            Classes where the individual belongs to.
	 * @param idValue
	 *            ID of the individual name (PREFIX + idValue is the invidual
	 *            name)
	 * @return
	 */
	private Individual createFullIndividual(PhiboURI URI, String prefix,
			OntClass[] ontClasses, String idValue) {
		return createFullIndividual(null, null, URI, prefix, ontClasses,
				idValue, null, null);
	}

	/**
	 * Another version of the method. Receives.
	 * 
	 * @param map
	 *            The map.
	 * @param URI
	 *            The URI.
	 * @param prefix
	 *            The prefix.
	 * @param ontClasses
	 *            The classes to be typed.
	 * @param idValue
	 *            The idValue.
	 * @param value
	 *            The value (for has_value)
	 * @return It returns the individual.
	 */
	private Individual createFullIndividual(Map<String, Individual> map,
			PhiboURI URI, String prefix, OntClass[] ontClasses, String idValue,
			String value) {
		return createFullIndividual(map, value, URI, prefix, ontClasses,
				idValue, null, null);
	}

	/**
	 * Generic method to create an individual.
	 * 
	 * @param map
	 *            Receives the map where previous "same-equivalent" individuals
	 *            could be stored. It checks here if we already have this
	 *            individual, and if we have it, we use it instead of creating a
	 *            new one.
	 * @param value
	 *            Receives the value which will be attached through the
	 *            "has_value" property.
	 * @param URI
	 *            Receives the URI that will have the individual.
	 * @param prefix
	 *            Receives the prefix that will have the individual.
	 * @param ontClasses
	 *            Receives the classes where the individual will belongs to.
	 * @param idValue
	 *            Receives the idNumberValue that the individual will have.
	 * @param label
	 *            Receives the label that the individual will have.
	 * @param sameAsMappings
	 *            Receives the sameAs mappings.
	 * @return Returns the individual.
	 */
	private Individual createFullIndividual(Map<String, Individual> map,
			String value, PhiboURI URI, String prefix, OntClass[] ontClasses,
			String idValue, String label, LinkedList<String> sameAsMappings) {
		Individual ind = null;

		if (value != null && map != null) {
			ind = map.get(value.toUpperCase());
			if (ind != null) {
				return ind;
			}
		}
		String indNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(idValue));
		String uri = null;
		if (URI instanceof LocalURI) {
			uri = Constants.ONT_URI_BASE_DATA + URI.getURI() + prefix + indNum;
		}
		if (URI instanceof ExternalURI) {
			uri = URI.getURI();
		}
		if (ontClasses.length == 1) {
			ind = this.model.createIndividual(uri, ontClasses[0]);
		}
		if (ontClasses.length > 1) {
			ind = this.model.createIndividual(uri, ontClasses[0]);
			for (int i = 1; i < ontClasses.length; i++) {
				ind.addOntClass(ontClasses[i]);
			}
		}
		if (label != null) {
			ind.setLabel(label, "en");
		} else {
			ind.setLabel(prefix + indNum, "en");
		}
		if (sameAsMappings != null) {
			for (int i = 0; i < sameAsMappings.size(); i++) {
				Resource mapping = model.createResource(sameAsMappings.get(i));
				String clMapping = getClassMapping(URI);
				if (mapping != null) {
					ind.addProperty(OWL.sameAs, mapping);
					if (clMapping != null) {
						OntClass clType = model.getOntClass(clMapping);
						if (clType != null) {
							clType.addLabel(model.createTypedLiteral(clType
									.getLabel(null)));
							ind.addOntClass(clType);
						}
					}
				}
			}
		}
		if (value != null) {
			ind.addProperty(this.has_value, value, "en");
			map.put(value.toUpperCase(), ind);
		}
		return ind;
	}

	/**
	 * Method to create an individual which has multiple values.
	 * 
	 * @param map
	 *            Receives the map where previous "same-equivalent" individuals
	 *            could be stored. It checks here if we already have this
	 *            individual, and if we have it, we use it instead of creating a
	 *            new one.
	 * @param values
	 *            Receives the value which will be attached through the
	 *            "has_value" property.
	 * @param URI
	 *            Receives the URI that will have the individual.
	 * @param prefix
	 *            Receives the prefix that will have the individual.
	 * @param ontClasses
	 *            Receives the classes where the individual will belongs to.
	 * @param idValue
	 *            Receives the idNumberValue that the individual will have.
	 * @param label
	 *            Receives the label that the individual will have.
	 * @param sameAsMappings
	 *            Receives the sameAs mappings.
	 * @return Returns the individual.
	 */
	private Individual createFullIndividualMultipleValues(
			Map<String, Individual> map, LinkedList<String> values,
			PhiboURI URI, String prefix, OntClass[] ontClasses, String idValue,
			String label, LinkedList<String> sameAsMappings) {
		Individual ind = null;

		if (values != null && map != null) {
			ind = map.get(StaticUtils.getStringFromList(values).toUpperCase());
			if (ind != null) {
				return ind;
			}
		}
		String indNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(idValue));
		String uri = null;
		if (URI instanceof LocalURI) {
			uri = Constants.ONT_URI_BASE_DATA + URI.getURI() + prefix + indNum;
		}
		if (URI instanceof ExternalURI) {
			uri = URI.getURI();
		}
		if (ontClasses.length == 1) {
			ind = this.model.createIndividual(uri, ontClasses[0]);
		}
		if (ontClasses.length > 1) {
			ind = this.model.createIndividual(uri, ontClasses[0]);
			for (int i = 1; i < ontClasses.length; i++) {
				ind.addOntClass(ontClasses[i]);
			}
		}
		if (label != null) {
			ind.setLabel(label, "en");
		} else {
			ind.setLabel(prefix + indNum, "en");
		}
		if (sameAsMappings != null) {
			for (int i = 0; i < sameAsMappings.size(); i++) {
				Resource mapping = model.createResource(sameAsMappings.get(i));
				String clMapping = getClassMapping(URI);
				if (mapping != null) {
					ind.addProperty(OWL.sameAs, mapping);
					if (clMapping != null) {
						OntClass clType = model.getOntClass(clMapping);
						if (clType != null) {
							clType.addLabel(model.createTypedLiteral(clType
									.getLabel(null)));
							ind.addOntClass(clType);
						}
					}
				}
			}
		}
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				String value = values.get(i);
				ind.addProperty(this.has_value, value, "en");
			}
			map.put(StaticUtils.getStringFromList(values).toUpperCase(), ind);
		}
		return ind;
	}

	/**
	 * Method to get the class used in a mapping to establish rdf:type of a
	 * given resource
	 * 
	 * @param uri
	 *            Receives the original URI to get the database associated and
	 *            the class associated to the database.
	 * @return The URI of the class.
	 */
	private String getClassMapping(PhiboURI uri) {
		String uriStr = uri.getURI();
		String filePart = uriStr.replace(Constants.IDS_ORG_BASE, "");
		filePart = filePart.replace('/', '_');
		filePart = filePart.replace('.', '_');
		String db = StaticUtils.getStringUntilLast(filePart, '_');
		String classURI = mappingsDBClassTypes.get(db);
		return classURI;
	}

	/**
	 * Method to create the gene individual.
	 * 
	 * @param pe
	 *            Receives the entry.
	 * @return Returns the individual.
	 */
	private PhiIndividual createGene(PhiEntry pe, Individual pathogen) {
		// System.out.println("\t\t\tCreating gene..");
		Individual gene = null;

		int geneHash = 0;
		String geneNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.GENE));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_GENE
				+ Constants.PREFIX_GENE + geneNum;
		gene = this.model.createIndividual(uri, this.class_gene_sio);
		gene.addOntClass(this.class_gene_sofa);
		gene.setLabel("Gene - " + Constants.PREFIX_GENE + geneNum, "en");

		long tSloc = System.currentTimeMillis();
		if (pe.getLocusID() != null) {
			Individual locusID = createFullIndividual(this.locusIDs,
					pe.getLocusID(), new LocalURI(Constants.URI_LOCUS_ID),
					Constants.PREFIX_LOCUS_ID,
					new OntClass[] { this.class_locus_id }, Constants.LOCUS_ID,
					"Locus ID - " + pe.getLocusID());

			geneHash += getHash(pe.getLocusID());

			if (locusID != null) {
				gene.addProperty(this.has_unique_identifier, locusID);
				locusID.addProperty(this.is_unique_identifier_for, gene);
			}
		}
		timeMeasure
				.calculateTime(
						tSloc,
						"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->locus]: ");

		long tSNTseq = System.currentTimeMillis();
		if (pe.getNTSequence() != null) {

			Individual ntseq = createFullIndividual(this.ntSeqs,
					pe.getNTSequence(), new LocalURI(Constants.URI_NT_SEQ),
					Constants.PREFIX_NT_SEQ,
					new OntClass[] { this.class_nt_seq },
					Constants.NT_SEQUENCE, null);

			geneHash += getHash(pe.getNTSequence());

			if (ntseq != null) {
				gene.addProperty(this.has_quality, ntseq);
				ntseq.addProperty(this.is_attribute_of, gene);
			}
		}
		timeMeasure
				.calculateTime(
						tSNTseq,
						"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->ntSeq]: ");
		long tSAAseq = System.currentTimeMillis();
		if (pe.getAASequence() != null) {
			Individual aaseq = createFullIndividual(this.aaSeqs,
					pe.getAASequence(), new LocalURI(Constants.URI_AA_SEQ),
					Constants.PREFIX_AA_SEQ,
					new OntClass[] { this.class_protein_sequence },
					Constants.AA_SEQUENCE, null);
			geneHash += getHash(pe.getAASequence());
			if (aaseq != null) {
				gene.addProperty(this.has_quality, aaseq);
				aaseq.addProperty(this.is_attribute_of, gene);
			}
		}
		geneHash += getHash(pe.getGeneName());
		timeMeasure
				.calculateTime(
						tSAAseq,
						"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->aaSeq]: ");
		long tSgn = System.currentTimeMillis();
		Individual geneName = createFullIndividual(this.geneNames,
				pe.getGeneName(), new LocalURI(Constants.URI_GENE_NAME),
				Constants.PREFIX_GENE_NAME,
				new OntClass[] { this.class_gene_name }, Constants.GENE_NAME,
				"Gene name - " + pe.getGeneName());
		if (geneName != null) {
			gene.addProperty(this.has_unique_identifier, geneName);
			geneName.addProperty(this.is_unique_identifier_for, gene);
		}

		timeMeasure
				.calculateTime(
						tSgn,
						"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->gene name]: ");

		geneHash += getHash(pe.getAccession());

		String dbType = null;
		if (pe.getDbType() != null) {
			dbType = pe.getDbType();
		} else {
			dbType = pe.getIdentifierTypeGeneLocusID();
		}

		long tSga = System.currentTimeMillis();
		PhiboURI geneAccURI = getGeneAccessionURI(dbType, pe.getAccession());
		Individual geneAcc = createFullIndividual(this.geneAccessions,
				pe.getAccession(), geneAccURI, Constants.PREFIX_GENE_ACCESSION,
				new OntClass[] { this.class_protein_accession },
				Constants.GENE_ACCESSION,
				"Gene accession - " + pe.getAccession(),
				getSameAsMappings(geneAccURI));
		if (geneAcc != null) {
			gene.addProperty(this.has_unique_identifier, geneAcc);
			geneAcc.addProperty(this.is_unique_identifier_for, gene);
		}
		geneHash += getHash(dbType);
		timeMeasure
				.calculateTime(
						tSga,
						"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->gene accession]: ");

		long tSgf = System.currentTimeMillis();

		if (pe.getFunction() != null) {
			Individual geneFunction = createFullIndividual(this.geneFunctions,
					pe.getFunction(), new LocalURI(Constants.URI_FUNCTION),
					Constants.PREFIX_FUNCTION,
					new OntClass[] { this.class_function },
					Constants.GENE_FUNCTION,
					"Gene function - " + pe.getFunction());
			if (geneFunction != null) {
				gene.addProperty(this.has_quality, geneFunction);
				geneFunction.addProperty(this.is_attribute_of, gene);

			}
			geneHash += getHash(pe.getFunction());

			timeMeasure
					.calculateTime(
							tSgf,
							"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->gene function]: ");
		}
		long tSgoas = System.currentTimeMillis();

		if (pe.getGOAnnotations() != null) {
			for (int i = 0; i < pe.getGOAnnotations().size(); i++) {
				String annot = pe.getGOAnnotations().get(i);
				geneHash += getHash(annot);
				if (annot != null) {
					Individual goAnnot = createFullIndividual(
							this.goAnnotations, annot,
							getGOAnnotationURI(annot),
							Constants.PREFIX_GO_ANNOTATION, new OntClass[] {
									this.class_go_concept_id,
									this.class_attribute },
							Constants.GO_ANNOTATION,
							"GO annotation - " + annot.replace(':', '_'));
					if (goAnnot != null) {
						gene.addProperty(this.has_quality, goAnnot);
						goAnnot.addProperty(this.is_attribute_of, gene);
					}
				}
			}
		}
		timeMeasure
				.calculateTime(
						tSgoas,
						"\t\t\t\t\tTotal time [createInteractionContext->pathogenContext->allele->gene->goas]: ");
		// PhiIndividual geneGMP = createGeneGMP(pe);
		// Individual geneGMPInd = geneGMP.getIndividual();
		//
		// geneHash += geneGMP.getHash();
		//
		// gene.addProperty(this.has_quality, geneGMPInd);
		// geneGMPInd.addProperty(this.is_attribute_of, gene);

		gene.addProperty(this.is_proper_part_of, pathogen);

		PhiIndividual gene_Existing = this.genes.get(geneHash);
		if (gene_Existing != null) {
			return gene_Existing;
		} else {
			PhiIndividual geneRet = new PhiIndividual(gene, geneHash);
			this.genes.put(geneHash, geneRet);
			return geneRet;
		}

	}

	private LinkedList<String> getSameAsMappings(PhiboURI uri) {
		// return null;
		if (uri instanceof ExternalURI) {
			try {
				LinkedList<String> mappings = getIdentifiersLocalMappings(uri);
				if (mappings == null) {
					SPARQLQueryEngine sqe = new SPARQLQueryEngine();
					ArrayList<Substitution> subs = new ArrayList<Substitution>();
					subs.add(new Substitution(Constants.URI_SUBSTITUTION, uri
							.getURI()));
					String fq = sqe.loadQueryFromFileWithSubstitutions(
							Constants.SPARQL_QUERY_FILE, subs);
					mappings = sqe.executeQuery(uri.getURI(), fq,
							Constants.IDS_ORG_SPARQL_ENDPOINT, "?uris");
					// checkOnlineMappings(mappings, fq, uri);
					saveIdentifiersLocalMappings(uri, mappings);
				}
				return mappings;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	// private void checkOnlineMappings(LinkedList<String> mappings, String fq,
	// PhiboURI uri) throws Exception {
	// boolean problemFound = false;
	// for (int i = 0; i < mappings.size(); i++) {
	// String mapping = mappings.get(i);
	// boolean reachable = StaticUtils.isURLReachable(mapping);
	// if (!reachable) {
	// if (!problemFound) {
	// StaticUtils.writeFile("offline_mappings.lst",
	// "URI checked @ identifiers.org: " + uri.getURI());
	// StaticUtils.writeFile("offline_mappings.lst", "");
	// StaticUtils.writeFile("offline_mappings.lst",
	// "URLs which fail:");
	// StaticUtils.writeFile("offline_mappings.lst", "");
	// problemFound = true;
	// }
	// StaticUtils.writeFile("offline_mappings.lst", "\tFail: "
	// + mapping);
	// }
	// }
	// if (problemFound) {
	// StaticUtils.writeFile("offline_mappings.lst", "");
	// StaticUtils.writeFile("offline_mappings.lst",
	// "------------------------------------------------");
	// StaticUtils.writeFile("offline_mappings.lst", "");
	// }
	//
	// }

	/**
	 * Method to save the mappings to a local file.
	 * 
	 * @param uri
	 *            Receives the associated URI.
	 * @param mappings
	 *            Receives the mappings.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void saveIdentifiersLocalMappings(PhiboURI uri,
			LinkedList<String> mappings) throws Exception {
		String uriStr = uri.getURI();
		String filePart = uriStr.replace(Constants.IDS_ORG_BASE, "");
		filePart = filePart.replace('/', '_');
		filePart = filePart.replace('.', '_');
		String fileStr = Constants.MAPPINGS_IDENTIFIERS_FOLDER + filePart;
		File file = new File(fileStr);
		BufferedWriter bW = new BufferedWriter(new FileWriter(file));
		if (mappings != null && mappings.size() > 0) {
			for (int i = 0; i < mappings.size(); i++) {
				bW.write(mappings.get(i));
				bW.newLine();
			}
		}
		bW.close();
	}

	/**
	 * Method to get the local mappings from an external file.
	 * 
	 * @param uri
	 *            Receives the URI.
	 * @return Return the list of mappings (if available).
	 */
	private LinkedList<String> getIdentifiersLocalMappings(PhiboURI uri) {
		String uriStr = uri.getURI();
		String filePart = uriStr.replace(Constants.IDS_ORG_BASE, "");
		filePart = filePart.replace('/', '_');
		filePart = filePart.replace('.', '_');
		String fileStr = Constants.MAPPINGS_IDENTIFIERS_FOLDER + filePart;
		File file = new File(fileStr);
		if (file.exists()) {
			try {
				LinkedList<String> mappings = new LinkedList<String>();
				BufferedReader bL = new BufferedReader(new FileReader(file));
				while (bL.ready()) {
					String rd = bL.readLine();
					mappings.add(rd);
				}
				bL.close();
				if (mappings.size() > 0) {
					return mappings;
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Method to get the URI of a go annotation.
	 * 
	 * @param annot
	 *            Receives the annotation.
	 * @return Returns the value.
	 */
	private PhiboURI getGOAnnotationURI(String annot) {
		return new ExternalURI(Constants.IDS_ORG_GOA + annot.trim());
	}

	/**
	 * Method to get the URI to be set to a gene accession individual.
	 * 
	 * @param dbType
	 *            Receives the dbtype.
	 * @param accession
	 *            Receives the accession value.
	 * @return Returns the value.
	 */
	private PhiboURI getGeneAccessionURI(String dbType, String accession) {
		String uriRet = null;
		if (accession != null) {
			if (accession.contains("|")) {
				uriRet = Constants.URI_GENE_ACCESSION;
				return new LocalURI(uriRet);
			}
			uriRet = null;
			accession = accession.replace(" ", "");
			if (StaticUtils.isEmpty(dbType)) {
				String possibleURI = geneAccessionURIsByUnknownDB
						.get(accession);
				if (possibleURI != null) {
					return new ExternalURI(possibleURI.trim());
				} else {
					uriRet = Constants.URI_GENE_ACCESSION;
					return new LocalURI(uriRet);
				}
			}

			/*
			 * Excepción para patrones concretos y usar refseq.
			 */
			String acc = accession.trim();
			Pattern p = Pattern
					.compile("^((AC|AP|NC|NG|NM|NP|NR|NT|NW|XM|XP|XR|YP|ZP)_\\d+|(NZ\\_[A-Z]{4}\\d+))(\\.\\d+)?$");
			Matcher m = p.matcher(acc);
			if (m.matches()) {
				String uri = "http://identifiers.org/refseq/" + acc + ".1";
				return new ExternalURI(uri.trim());
			}

			/**
			 * 
			 * EXCEPCION TEMPORAL PARA ENTREZ_PROTEIN (ena-embl)
			 * 
			 */
			if (dbType.toUpperCase().trim().equalsIgnoreCase("ENTREZ PROTEIN")) {

				if (acc.toUpperCase().startsWith("MGG_")) {
					String uri = "http://fungi.ensembl.org/id/" + acc;
					return new ExternalURI(uri.trim());
				} else {
					String uri = "http://purl.uniprot.org/embl-cds/" + acc
							+ ".1";
					return new ExternalURI(uri.trim());
				}
			} else {
				String uri = this.geneAccessionURIsByKnownDB.get(dbType
						.toUpperCase().trim());
				if (uri != null) {
					uri = uri.trim();
					if (uri.equalsIgnoreCase(Constants.NO_VALUE)) {
						String possibleURI = geneAccessionURIsByUnknownDB
								.get(accession.trim());
						if (possibleURI != null) {
							return new ExternalURI(possibleURI.trim());
						} else {
							uriRet = Constants.URI_GENE_ACCESSION;
							return new LocalURI(uriRet);
						}
					} else {
						uriRet = uri + accession;
						return new ExternalURI(uriRet.trim());
					}
				}
			}
		} else {
			return new LocalURI(Constants.URI_GENE_ACCESSION);
		}
		return new LocalURI(Constants.URI_GENE_ACCESSION);
	}

	/**
	 * Method to get the taxonomy ID URI.
	 * 
	 * @param id
	 *            Receives the ID.
	 * @return The URI.
	 */
	private PhiboURI getTaxonomyIDURI(int id, String baseURI) {
		if (id == -1) {
			return new LocalURI(baseURI);
		} else {
			return new ExternalURI(Constants.IDS_ORG_TAXONOMY + id);
		}
	}

	/**
	 * Method to get the pubmed ID URI of a given publication.
	 * 
	 * @param id
	 *            Receives the ID.
	 * @param baseURI
	 *            Receives the baseURI to use.
	 * @return The value
	 */
	private PhiboURI getPubMedIDURI(int id, String baseURI) {
		if (id == -1) {
			return new LocalURI(baseURI);
		} else {
			return new ExternalURI(Constants.IDS_ORG_PUBMED + id);
		}
	}

	// /**
	// * Method to create the gene mutation parameters individual.
	// *
	// * @param pe
	// * Receives the entry.
	// * @return Returns the individual.
	// */
	// private PhiIndividual createGeneGMP(PhiEntry pe) {
	// // System.out.println("\t\t\t\tCreating GMP..");
	//
	// Individual geneGMP = null;
	// int gmpHash = 0;
	// String geneGMPNum = String.format(Constants.ZERO_FILL_FORMAT,
	// getIDNumber(Constants.GENE_GMP));
	// String uri = Constants.ONT_URI_BASE + Constants.URI_GMP
	// + Constants.PREFIX_GMP + geneGMPNum;
	// geneGMP = this.model.createIndividual(uri,
	// this.class_gene_mutation_parameter);
	// geneGMP.setLabel("GMP - " + Constants.PREFIX_GMP + geneGMPNum, "en");
	//
	// for (int i = 0; i < pe.getExperimentalEvidences().size(); i++) {
	// gmpHash += getHash(pe.getExperimentalEvidences().get(i));
	// Individual experimentalEvidence = createFullIndividual(
	// this.experimentalEvidences, pe.getExperimentalEvidences()
	// .get(i), new LocalURI(
	// Constants.URI_EXPERIMENTAL_EVIDENCE),
	// Constants.PREFIX_EXPERIMENTAL_EVIDENCE, new OntClass[] {
	// getClassForExperimentalEvidence(pe
	// .getExperimentalEvidences().get(i)),
	// this.class_skos_concept },
	// Constants.EXPERIMENTAL_EVIDENCES,
	// "Experimental evidence - "
	// + pe.getExperimentalEvidences().get(i));
	// geneGMP.addProperty(this.has_quality, experimentalEvidence);
	// experimentalEvidence.addProperty(this.in_scheme,
	// this.eso_individual);
	// experimentalEvidence.addProperty(this.is_attribute_of, geneGMP);
	// }
	// gmpHash += getHash(pe.getPhenotypeOfMutant());
	// Individual mutationPhenotype = createFullIndividual(
	// this.mutationPhenotypes,
	// pe.getPhenotypeOfMutant(),
	// new LocalURI(Constants.URI_MUTATION_PHENOTYPE),
	// Constants.PREFIX_MUTATION_PHENOTYPE,
	// new OntClass[] {
	// getClassForMutationPhenotype(pe.getPhenotypeOfMutant()),
	// this.class_skos_concept },
	// Constants.MUTATION_PHENOTYPE,
	// "Mutation phenotype - " + pe.getPhenotypeOfMutant());
	// if (mutationPhenotype != null) {
	// geneGMP.addProperty(this.has_quality, mutationPhenotype);
	// mutationPhenotype.addProperty(this.is_attribute_of, geneGMP);
	// mutationPhenotype.addProperty(this.in_scheme, this.poo_individual);
	// }
	//
	// PhiIndividual geneGMP_Existing = this.geneGMPs.get(gmpHash);
	// if (geneGMP_Existing != null) {
	// return geneGMP_Existing;
	// } else {
	// PhiIndividual geneGMPRet = new PhiIndividual(geneGMP, gmpHash);
	// this.geneGMPs.put(gmpHash, geneGMPRet);
	// return geneGMPRet;
	// }
	// }

	/**
	 * Method to obtain the correct class for a given mutation phenotype.
	 * 
	 * @param pom
	 *            Receives the value of the mutation phenotype.
	 * @return Returns the associated class.
	 */
	private OntClass getClassForMutationPhenotype(String pom) {
		String ontClass = this.mutantPhenotypesOntClasses.get(pom);
		if (ontClass != null) {
			return model.getOntClass(ontClass);
		} else {
			return model.getOntClass(Constants.ONT_CLASS_POO);
		}
	}

	/**
	 * Method to obtain the correct class for a given experimental evidence.
	 * 
	 * @param pom
	 *            Receives the value of the experimental evidence.
	 * @return Returns the associated class.
	 */
	private OntClass getClassForExperimentalEvidence(String ee) {
		String ontClass = this.experimentalEvidencesOntClasses.get(ee);
		if (ontClass != null) {
			return model.getOntClass(ontClass);
		} else {
			return model
					.getOntClass(Constants.ONT_CLASS_EXPERIMENT_SPECIFICATION);
		}
	}

	/**
	 * Method to create the pathogen individual.
	 * 
	 * @param pe
	 *            Receives the entry.
	 * @return Returns the individual.
	 */
	private PhiIndividual createPathogen(PhiEntry pe) {
		// System.out.println("\t\t\tCreating pathogen..");
		Individual pathogen = null;

		int pathogenHash = 0;

		String pathogenNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.PATHOGEN));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_PATHOGEN
				+ Constants.PREFIX_PATHOGEN + pathogenNum;
		// uri or the current one (getTaxonomyIDURI..
		// getTaxonomyIDURI(pe.getPathogenNcbiTaxonomyID(),Constants.URI_PATHOGEN_NCBI_TAXONOMY_ID).getURI()
		pathogen = this.model.createIndividual(uri, this.class_pathogen_sio);
		pathogen.addOntClass(this.class_pathogen_efo);
		pathogen.addOntClass(this.class_organism_obi);
		pathogen.addOntClass(this.class_organism_sio);
		pathogen.setLabel("Pathogen - " + pe.getPathogenSpecies() + " - "
				+ Constants.PREFIX_PATHOGEN + pathogenNum, "en");

		// Individual pathSpecies = createFullIndividual(this.species,
		// pe.getPathogenSpecies(), new LocalURI(
		// Constants.URI_PATHOGEN_SPECIES),
		// Constants.PREFIX_PATHOGEN_SPECIES,
		// new OntClass[] { this.class_scientific_name },
		// Constants.SPECIES,
		// "Pathogen species - " + pe.getPathogenSpecies());
		//
		// pathogenHash += getHash(pe.getPathogenSpecies());
		//
		// if (pathSpecies != null) {
		// pathogen.addProperty(this.has_quality, pathSpecies);
		// pathSpecies.addProperty(this.is_attribute_of, pathogen);
		// }
		long tSntp = System.currentTimeMillis();
		PhiIndividual ncbiTaxon = createNCBITaxonPathogen(pe);
		Individual ncbiTaxonInd = ncbiTaxon.getIndividual();
		pathogenHash += ncbiTaxon.getHash();

		if (ncbiTaxonInd != null) {
			pathogen.addProperty(this.is_member_of, ncbiTaxonInd);
			ncbiTaxonInd.addProperty(this.has_member, pathogen);
		}
		timeMeasure.calculateTime(tSntp,
				"\t\tTotal time [createPathogen->createNCBITaxonPathogen]: ");
		// Individual pathNCBITaxID = createFullIndividual(
		// this.ncbiTaxonomyIDs,
		// Integer.toString(pe.getPathogenNcbiTaxonomyID()),
		// getTaxonomyIDURI(pe.getPathogenNcbiTaxonomyID(),
		// Constants.URI_PATHOGEN_NCBI_TAXONOMY_ID),
		// Constants.PREFIX_PATHOGEN_NCBI_TAXONOMY_ID,
		// new OntClass[] { this.class_ncbi_taxonomy_id },
		// Constants.PATHOGEN_NCBI_TAXONOMY_ID,
		// "Pathogen NCBI Taxon ID - " + pe.getPathogenNcbiTaxonomyID());
		//
		// pathogenHash += getHash(Integer
		// .toString(pe.getPathogenNcbiTaxonomyID()));
		//
		// if (pathNCBITaxID != null) {
		// pathogen.addProperty(this.has_unique_identifier, pathNCBITaxID);
		// pathNCBITaxID.addProperty(this.is_unique_identifier_for, pathogen);
		// }

		long tSasst = System.currentTimeMillis();
		if (pe.getAssociatedStrain() != null) {
			Individual pathAssociatedStrain = createFullIndividual(
					this.pathogenAssociatedStrains, pe.getAssociatedStrain(),
					new LocalURI(Constants.URI_PATHOGEN_ASSOCIATED_STRAIN),
					Constants.PREFIX_PATHOGEN_ASSOCIATED_STRAIN,
					new OntClass[] { this.class_associated_strain },
					Constants.PATHOGEN_ASSOCIATED_STRAIN,
					"Pathogen Associated Strain - " + pe.getAssociatedStrain());

			pathogenHash += getHash(pe.getAssociatedStrain());

			if (pathAssociatedStrain != null) {
				pathogen.addProperty(this.has_quality, pathAssociatedStrain);
				pathAssociatedStrain
						.addProperty(this.is_attribute_of, pathogen);
			}
		}
		timeMeasure.calculateTime(tSasst,
				"\t\tTotal time [createPathogen->associatedStrain]: ");
		long tSst = System.currentTimeMillis();
		if (pe.getStrain() != null) {
			Individual pathStrain = createFullIndividual(this.pathogenStrains,
					pe.getStrain(),
					new LocalURI(Constants.URI_PATHOGEN_STRAIN),
					Constants.PREFIX_PATHOGEN_STRAIN,
					new OntClass[] { this.class_strain },
					Constants.PATHOGEN_STRAIN,
					"Pathogen Strain - " + pe.getStrain());

			pathogenHash += getHash(pe.getStrain());

			if (pathStrain != null) {
				pathogen.addProperty(this.has_quality, pathStrain);
				pathStrain.addProperty(this.is_attribute_of, pathogen);
			}
		}
		timeMeasure.calculateTime(tSst,
				"\t\tTotal time [createPathogen->strain]: ");

		PhiIndividual pathogenRet = new PhiIndividual(pathogen, pathogenHash);
		pathogenRet.setAuxiliarIndividual(ncbiTaxonInd);
		// this.pathogens.put(pathogenHash, pathogenRet);
		return pathogenRet;

		// PhiIndividual pathogen_Existing = this.pathogens.get(pathogenHash);
		// if (pathogen_Existing != null) {
		// return pathogen_Existing;
		// } else {
		// PhiIndividual pathogenRet = new PhiIndividual(pathogen,
		// pathogenHash);
		// this.pathogens.put(pathogenHash, pathogenRet);
		// return pathogenRet;
		// }
	}

	private PhiIndividual createNCBITaxonHost(PhiEntry pe) {
		Individual hostTaxon = null;
		int hostTaxonHash = 0;
		String uri = getTaxonomyIDURI(pe.getHostNcbiTaxonomyID(),
				Constants.URI_HOST_NCBI_TAXONOMY_ID).getURI();
		hostTaxon = this.model.createIndividual(uri,
				this.class_ncbi_taxonomy_id);
		if (!StaticUtils.isEmpty(pe.getExperimentalHost())) {
			hostTaxon.setLabel(pe.getExperimentalHost(), "en");
		} else {
			hostTaxon.setLabel(Constants.PREFIX_HOST_NCBI_TAXONOMY_ID
					+ " No value", "en");
		}
		hostTaxon.addProperty(this.has_value,
				"taxon:" + pe.getHostNcbiTaxonomyID(), "en");

		hostTaxonHash = pe.getHostNcbiTaxonomyID();

		PhiIndividual hostTaxon_Existing = this.hostTaxons.get(hostTaxonHash);
		if (hostTaxon_Existing != null) {
			return hostTaxon_Existing;
		} else {
			PhiIndividual hostTaxonRet = new PhiIndividual(hostTaxon,
					hostTaxonHash);
			this.hostTaxons.put(hostTaxonHash, hostTaxonRet);
			return hostTaxonRet;
		}
	}

	private PhiIndividual createNCBITaxonPathogen(PhiEntry pe) {
		Individual pathogenTaxon = null;
		int pathogenTaxonHash = 0;
		String uri = getTaxonomyIDURI(pe.getPathogenNcbiTaxonomyID(),
				Constants.URI_PATHOGEN_NCBI_TAXONOMY_ID).getURI();
		pathogenTaxon = this.model.createIndividual(uri,
				this.class_ncbi_taxonomy_id);
		pathogenTaxon.setLabel(pe.getPathogenSpecies(), "en");
		pathogenTaxon.addProperty(this.has_value,
				"taxon:" + pe.getPathogenNcbiTaxonomyID(), "en");

		pathogenTaxonHash = pe.getPathogenNcbiTaxonomyID();

		PhiIndividual pathogenTaxon_Existing = this.pathogenTaxons
				.get(pathogenTaxonHash);
		if (pathogenTaxon_Existing != null) {
			return pathogenTaxon_Existing;
		} else {
			PhiIndividual pathogenTaxonRet = new PhiIndividual(pathogenTaxon,
					pathogenTaxonHash);
			this.pathogenTaxons.put(pathogenTaxonHash, pathogenTaxonRet);
			return pathogenTaxonRet;
		}
	}

	private PhiIndividual createInteractionContext(PhiEntry pe, boolean wt,
			Individual pathogen) {
		Individual interactionContext = null;
		int interactionContextHash = 0;
		String intContextNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.INTERACTION_CONTEXT));
		String uri = Constants.ONT_URI_BASE_DATA
				+ Constants.URI_INTERACTION_CONTEXT
				+ Constants.PREFIX_INTERACTION_CONTEXT + intContextNum;
		interactionContext = this.model.createIndividual(uri,
				this.class_interaction_context);
		String icType = wt ? "[WT]" : "[MUT]";
		interactionContext.setLabel("Interaction context - " + icType + " "
				+ Constants.PREFIX_INTERACTION_CONTEXT + intContextNum, "en");

		// PATHOGEN CONTEXT
		long tSpc = System.currentTimeMillis();
		PhiIndividual pathogenContext = createPathogenContext(pe, wt, pathogen);
		Individual pathContextInd = pathogenContext.getIndividual();

		interactionContextHash += pathogenContext.getHash();

		interactionContext.addProperty(this.depends_on, pathContextInd);
		pathContextInd.addProperty(this.affects, interactionContext);
		// no inverse for "depends_on"

		timeMeasure.calculateTime(tSpc,
				"\t\tTotal time [createInteractionContext->pathogenContext]: ");
		// HOST CONTEXT
		long tShc = System.currentTimeMillis();
		PhiIndividual hostContext = createHostContext(pe, wt);
		Individual hostContextInd = hostContext.getIndividual();

		interactionContextHash += hostContext.getHash();

		interactionContext.addProperty(this.depends_on, hostContextInd);
		// no inverse for "depends_on"
		timeMeasure.calculateTime(tShc,
				"\t\tTotal time [createInteractionContext->hostContext]: ");
		// DESCRIPTION

		long tSdsc = System.currentTimeMillis();

		PhiIndividual description = createDescription(pe, wt);
		Individual descriptionInd = description.getIndividual();

		interactionContextHash += description.getHash();

		interactionContext.addProperty(this.has_quality, descriptionInd);
		timeMeasure.calculateTime(tSdsc,
				"\t\tTotal time [createInteractionContext->description]: ");

		// PROTOCOL
		long tSprot = System.currentTimeMillis();

		PhiIndividual protocol = createProtocol(pe, wt);
		Individual protocolInd = protocol.getIndividual();

		interactionContextHash += protocol.getHash();

		interactionContext.addProperty(this.is_output_of, protocolInd);
		protocolInd.addProperty(this.has_output, interactionContext);
		timeMeasure.calculateTime(tSprot,
				"\t\tTotal time [createInteractionContext->protocol]: ");

		PhiIndividual interactionContext_Existing = this.interactionContexts
				.get(interactionContextHash);
		if (interactionContext_Existing != null) {
			return interactionContext_Existing;
		} else {
			PhiIndividual interactionContextRet = new PhiIndividual(
					interactionContext, interactionContextHash);
			this.interactionContexts.put(interactionContextHash,
					interactionContextRet);
			return interactionContextRet;
		}

	}

	/**
	 * Creates the experimental host individual.
	 *
	 * @param pe
	 *            Receives the phientry object.
	 * @return The individual.
	 */
	private PhiIndividual createHost(PhiEntry pe) {
		// System.out.println("\t\tCreating experimental host..");

		Individual host = null;
		int hostHash = 0;
		String hostNum = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.HOST));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_HOST
				+ Constants.PREFIX_HOST + hostNum;
		// getTaxonomyIDURI(pe.getHostNcbiTaxonomyID(),Constants.URI_HOST_NCBI_TAXONOMY_ID).getURI()
		// or "uri"
		host = this.model.createIndividual(uri, this.class_host_sio);
		host.addOntClass(this.class_host_efo);
		host.addOntClass(this.class_organism_obi);
		host.addOntClass(this.class_organism_sio);
		host.setLabel("Host - " + getScientificNameOf(pe.getExperimentalHost())
				+ " - " + Constants.PREFIX_HOST + hostNum, "en");

		// Individual hostNCBITaxonomyID = createFullIndividual(
		// this.ncbiTaxonomyIDs,
		// Integer.toString(pe.getHostNcbiTaxonomyID()),
		// getTaxonomyIDURI(pe.getHostNcbiTaxonomyID(),
		// Constants.URI_HOST_NCBI_TAXONOMY_ID),
		// Constants.PREFIX_HOST_NCBI_TAXONOMY_ID,
		// new OntClass[] { this.class_ncbi_taxonomy_id },
		// Constants.HOST_NCBI_TAXONOMY_ID,
		// "Host NCBI Taxon ID - " + pe.getHostNcbiTaxonomyID());
		//
		// hostHash += getHash(Integer.toString(pe.getHostNcbiTaxonomyID()));
		//
		// if (hostNCBITaxonomyID != null) {
		// host.addProperty(this.has_unique_identifier, hostNCBITaxonomyID);
		// hostNCBITaxonomyID.addProperty(this.is_unique_identifier_for, host);
		// }
		long tSnth = System.currentTimeMillis();
		PhiIndividual ncbiTaxon = createNCBITaxonHost(pe);
		Individual ncbiTaxonInd = ncbiTaxon.getIndividual();

		hostHash += ncbiTaxon.getHash();

		if (ncbiTaxonInd != null) {
			host.addProperty(this.is_member_of, ncbiTaxonInd);
			ncbiTaxonInd.addProperty(this.has_member, host);
		}
		timeMeasure.calculateTime(tSnth,
				"\t\tTotal time [createHost->createNCBITaxonHost]: ");
		long tShs = System.currentTimeMillis();
		Individual hostSpecies = createFullIndividual(
				this.species,
				getScientificNameOf(pe.getExperimentalHost()),
				new LocalURI(Constants.URI_HOST_SPECIES),
				Constants.PREFIX_HOST_SPECIES,
				new OntClass[] { this.class_scientific_name },
				Constants.SPECIES,
				"Host species - "
						+ getScientificNameOf(pe.getExperimentalHost()));

		hostHash += getHash(pe.getExperimentalHost());

		if (hostSpecies != null) {
			host.addProperty(this.has_quality, hostSpecies);
			hostSpecies.addProperty(this.is_attribute_of, host);
		}
		timeMeasure.calculateTime(tShs,
				"\t\tTotal time [createHost->hostSpecies]: ");

		long tShr = System.currentTimeMillis();
		if (pe.getHostResponse() != null) {
			Individual hostResponse = createFullIndividual(this.hostResponses,
					pe.getHostResponse(), new LocalURI(
							Constants.URI_HOST_RESPONSE),
					Constants.PREFIX_HOST_RESPONSE,
					new OntClass[] { this.class_host_response },
					Constants.HOST_RESPONSE,
					"Host response - " + pe.getHostResponse());

			hostHash += getHash(pe.getHostResponse());

			if (hostResponse != null) {
				host.addProperty(this.has_quality, hostResponse);
				hostResponse.addProperty(this.is_attribute_of, host);
			}
			timeMeasure.calculateTime(tShr,
					"\t\tTotal time [createHost->hostResponse]: ");
		}
		PhiIndividual hostRet = new PhiIndividual(host, hostHash);
		// this.hosts.put(hostHash, hostRet);
		return hostRet;

		// PhiIndividual host_Existing = this.hosts.get(hostHash);
		// if (host_Existing != null) {
		// return host_Existing;
		// } else {
		// PhiIndividual hostRet = new PhiIndividual(host, hostHash);
		// this.hosts.put(hostHash, hostRet);
		// return hostRet;
		// }
	}

	/**
	 * Method to get the hashCode() value of a String. Returns 0 if string is
	 * null.
	 * 
	 * @param s
	 *            The string.
	 * @return The value.
	 */
	private int getHash(String s) {
		if (s == null) {
			return 0;
		} else {
			return s.hashCode();
		}
	}

	/**
	 * Creates the PHI accession number individual.
	 * 
	 * @param pe
	 *            Receives the phientry object.
	 * @return The individual.
	 */
	private Individual createPHIUniqueIdentifier(PhiEntry pe) {
		String phibaseidNumber = String.format(Constants.ZERO_FILL_FORMAT,
				getIDNumber(Constants.PHI_BASE_ID));
		String uri = Constants.ONT_URI_BASE_DATA + Constants.URI_PHI_BASE_ID
				+ Constants.PREFIX_PHI_BASE_ID + phibaseidNumber;
		Individual phibaseID = this.model.createIndividual(uri,
				this.class_phi_base_accession);
		if (phibaseID != null) {
			phibaseID.setLabel("Phi Base ID - " + Constants.PREFIX_PHI_BASE_ID
					+ phibaseidNumber, "en");
			phibaseID.addProperty(this.has_value,
					"PHI:" + pe.getPhiBaseAccessionNumber());

		}
		return phibaseID;
	}

	/**
	 * Method to get the id number that should be associated to a concrete type
	 * of individual.
	 * 
	 * @param p
	 *            Receives the type of individual.
	 * @return Returns the id number.
	 */
	public int getIDNumber(String p) {
		int valueNumber = this.idNumericValues.get(p);
		this.idNumericValues.put(p, valueNumber + 1);
		return valueNumber;
	}

	/**
	 * Method to get the scientific name of a given species.
	 * 
	 * @param species
	 *            Receives the species.
	 * @return Returns the value.
	 */
	private String getScientificNameOf(String species) {
		if (species != null) {
			species = species.toUpperCase();
			String scientificName = this.scientificNames.get(species);
			if (scientificName == null) {
				return "N/A";
			}
			return scientificName;
		}
		return null;
	}
}
