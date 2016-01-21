package es.upm.cbgp.semphi.logic;

public class Constants {

	public final static String HOSTS_EXCLUDED_FILE = "filters/hosts_excluded.lst";
	public final static String PATHOGENS_EXCLUDED_FILE = "filters/pathogens_excluded.lst";

	public final static String NCBI_TAXONOMY_NAMES_FILE = "data/ncbitax/names.dmp";
	public final static String NCBI_TAXONOMY_NODES_FILE = "data/ncbitax/nodes.dmp";

	public final static String NCBI_TAXONOMY_HIERARCHIES = "data/ncbitaxhierarchies.ppio";

	public final static String GO_SQL_HOST = "mysql-amigo.ebi.ac.uk";
	public final static int GO_SQL_PORT = 4085;
	public final static String GO_SQL_USER = "go_select";
	public final static String GO_SQL_PASSWORD = "amigo";
	public final static String GO_SQL_DATABASE = "go_latest";

	public final static String ONT_SCHEMA_FILE = "ont/PHI_schema.owl";
	public final static String ONT_URI_BASE_SCHEMA = "http://linkeddata.systems/ontologies/SemanticPHIBase";
	public final static String ONT_URI_BASE_DATA = "http://linkeddata.systems/SemanticPHIBase/Resource";
	public final static String ONT_SAVE_FILE = "ont/PHI_populated.owl";

	public final static String ONT_PROP_HAS_VALUE = "http://semanticscience.org/resource/SIO_000300";
	public final static String ONT_PROP_HAS_VALUE_PPIO = ONT_URI_BASE_SCHEMA
			+ "#has_value";
	
	public final static String ONT_PROP_HAS_QUALITY = "http://semanticscience.org/resource/SIO_000008";
	public final static String ONT_PROP_HAS_QUALITY_PPIO = ONT_URI_BASE_SCHEMA
			+ "#has_quality";
	
	public final static String ONT_PROP_IS_ATTRIBUTE_OF = "http://semanticscience.org/resource/SIO_000011";
	public final static String ONT_PROP_IS_ATTRIBUTE_OF_PPIO = ONT_URI_BASE_SCHEMA
			+ "#is_attribute_of";
	
	public final static String ONT_PROP_HAS_UNIQUE_IDENTIFIER = "http://semanticscience.org/resource/SIO_000673";
	public final static String ONT_PROP_HAS_UNIQUE_IDENTIFIER_PPIO = ONT_URI_BASE_SCHEMA
			+ "#has_unique_identifier";
	
	public final static String ONT_PROP_IS_UNIQUE_IDENTIFIER_FOR = "http://semanticscience.org/resource/SIO_000674";
	public final static String ONT_PROP_IS_UNIQUE_IDENTIFIER_FOR_PPIO = ONT_URI_BASE_SCHEMA
			+ "#is_unique_identifier_for";
	
	public final static String ONT_PROP_IS_VARIANT_OF = "http://semanticscience.org/resource/SIO_000272";
	public final static String ONT_PROP_IS_VARIANT_OF_PPIO = ONT_URI_BASE_SCHEMA
			+ "#is_variant_of";
	
	public final static String ONT_PROP_DEPENDS_ON = "http://purl.obolibrary.org/obo/RO_0002502";
	public final static String ONT_PROP_DEPENDS_ON_PPIO = ONT_URI_BASE_SCHEMA
			+ "#depends_on";
	
	public final static String ONT_PROP_IS_PROPER_PART_OF = "http://semanticscience.org/resource/SIO_000093";
	public final static String ONT_PROP_IS_PROPER_PART_OF_PPIO = ONT_URI_BASE_SCHEMA
			+ "#is_proper_part_of";
	
	public final static String ONT_PROP_HAS_PART = "http://semanticscience.org/resource/SIO_000053";
	public final static String ONT_PROP_HAS_PART_PPIO = ONT_URI_BASE_SCHEMA + "#has_proper_part";
	
	public final static String ONT_PROP_IS_DESCRIBED_BY = "http://semanticscience.org/resource/SIO_000557";
	public final static String ONT_PROP_IS_DESCRIBED_BY_PPIO = ONT_URI_BASE_SCHEMA + "#is_described_by";
	
	public final static String ONT_PROP_HAS_PARTICIPANT = "http://purl.obolibrary.org/obo/RO_0000057";
	public final static String ONT_PROP_HAS_PARTICIPANT_PPIO = ONT_URI_BASE_SCHEMA + "#has_participant";
	
	public final static String ONT_PROP_PARTICIPATES_IN = "http://purl.obolibrary.org/obo/RO_0000056";
	public final static String ONT_PROP_PARTICIPATES_IN_PPIO = ONT_URI_BASE_SCHEMA + "#participates_in";
	
	public final static String ONT_PROP_AFFECTS = "http://semanticscience.org/resource/SIO_001158";
	public final static String ONT_PROP_AFFECTS_PPIO = ONT_URI_BASE_SCHEMA + "#affects";
	
	public final static String ONT_PROP_IS_OUTPUT_OF = "http://semanticscience.org/resource/SIO_000232";
	public final static String ONT_PROP_IS_OUTPUT_OF_PPIO = ONT_URI_BASE_SCHEMA + "#is_output_of";
	
	public final static String ONT_PROP_HAS_OUTPUT = "http://semanticscience.org/resource/SIO_000229";
	public final static String ONT_PROP_HAS_OUTPUT_PPIO = ONT_URI_BASE_SCHEMA + "#has_output";
	
	public final static String ONT_PROP_IS_MEMBER_OF = "http://semanticscience.org/resource/SIO_000095";
	public final static String ONT_PROP_IS_MEMBER_OF_PPIO = ONT_URI_BASE_SCHEMA + "#is_member_of";

	public final static String ONT_PROP_IS_MANIFESTED_AS = "http://semanticscience.org/resource/SIO_000341";
	public final static String ONT_PROP_IS_MANIFESTED_AS_PPIO = ONT_URI_BASE_SCHEMA
			+ "#is_manifested_as";
	
	public final static String ONT_PROP_IS_MANIFESTATION_OF = "http://semanticscience.org/resource/SIO_000426";
	public final static String ONT_PROP_IS_MANIFESTATION_OF_PPIO = ONT_URI_BASE_SCHEMA
			+ "#is_manifestation_of";
	
	public final static String ONT_PROP_HAS_MEMBER = "http://purl.obolibrary.org/obo/RO_0002351";
	public final static String ONT_PROP_HAS_MEMBER_PPIO = ONT_URI_BASE_SCHEMA + "#has_member";
	
	public final static String ONT_PROP_IN_SCHEME = "http://www.w3.org/2004/02/skos/core#inScheme";
	public final static String ONT_PROP_BROADER = "http://www.w3.org/2004/02/skos/core#broader";
	public final static String ONT_PROP_NARROWER = "http://www.w3.org/2004/02/skos/core#narrower";

	
	public final static String ONT_INDIVIDUAL_ESO = ONT_URI_BASE_SCHEMA
			+ "#ESO_SCHEMA";
	public final static String ONT_INDIVIDUAL_POO = ONT_URI_BASE_SCHEMA
			+ "#POO_SCHEMA";

	public final static String SCIENTIFIC_NAMES_FILE = "phi_data/mappings/scientific_names.lst";
	public final static String MUTANT_PHENOTYPES_FILE = "phi_data/mappings/mutant_phenotypes.lst";
	public final static String EXPERIMENTAL_EVIDENCES_FILE = "phi_data/mappings/experimental_evidences.lst";
	public final static String GENE_ACCESSIONS_URIS_UNKNOWN_DBS_FILE = "phi_data/mappings/gene_accessions_unknown_dbs_uris.lst";
	public final static String GENE_ACCESSIONS_URIS_KNOWN_DBS_FILE = "phi_data/mappings/gene_accessions_known_dbs_uris_";
	public final static String DISEASES_NAMES_URIS_FILE = "phi_data/mappings/diseases_names_uris.lst";
	public final static String EC2GO_MAPPINGS_FILE = "phi_data/mappings/ec2go.lst";
	public final static String IVG_MAPPINGS_FILE = "phi_data/mappings/ivg.lst";

	public final static String NO_VALUE = "NO_VALUE";

	public final static String URI = "URI";
	public final static String SEE_ALSO = "SEE_ALSO";
	/*
	 * SPARQL
	 */
	public final static String URI_SUBSTITUTION = "@URI";
	public final static String SPARQL_QUERY_FILE = "phi_data/sparql.query";
	public final static String IDS_ORG_SPARQL_ENDPOINT = "http://identifiers.org/services/sparql";

	public final static String MAPPINGS_IDENTIFIERS_FOLDER = "phi_data/mappings/identifiers/";
	public final static String MAPPINGS_DBS_CLASS_TYPE_FILE = "phi_data/mappings/mapping_db_class_type.lst";

	public final static String IDS_ORG_BASE = "http://identifiers.org/";
	public final static String IDS_ORG_TAXONOMY = "http://identifiers.org/taxonomy/";
	public final static String IDS_ORG_GOA = "http://identifiers.org/go/";
	public final static String IDS_ORG_PUBMED = "http://identifiers.org/pubmed/";
	// EDAM

	// public final static String ONT_CLASS_PROTEIN_SEQUENCE =
	// "http://identifiers.org/edam/data_2976";
	// public final static String ONT_CLASS_PROTEIN_ACCESSION =
	// "http://identifiers.org/edam/data_2907";
	// public final static String ONT_CLASS_GENE_NAME =
	// "http://identifiers.org/edam/data_2299";
	// public final static String ONT_CLASS_NCBI_TAXONOMY_ID =
	// "http://identifiers.org/edam/data_1179";
	// public final static String ONT_CLASS_LOCUS_ID =
	// "http://identifiers.org/edam/data_1893";
	// public final static String ONT_CLASS_NT_SEQ =
	// "http://identifiers.org/edam/data_2977";
	// public final static String ONT_CLASS_GO_CONCEPT_ID =
	// "http://identifiers.org/edam/data_1176";

	public final static String ONT_CLASS_GO_CONCEPT_ID = "http://edamontology.org/data_1176";
	public final static String ONT_CLASS_PROTEIN_SEQUENCE = "http://edamontology.org/data_2976";
	public final static String ONT_CLASS_PROTEIN_ACCESSION = "http://edamontology.org/data_2907";
	public final static String ONT_CLASS_GENE_NAME = "http://edamontology.org/data_2299";
	public final static String ONT_CLASS_NCBI_TAXONOMY_ID = "http://edamontology.org/data_1179";
	public final static String ONT_CLASS_LOCUS_ID = "http://edamontology.org/data_1893";
	public final static String ONT_CLASS_NT_SEQ = "http://edamontology.org/data_2977";
	public final static String ONT_CLASS_PUBMED_IDENTIFIER = "http://edamontology.org/data_1187";
	//OBO
	
	public final static String ONT_CLASS_ORGANISM_OBI = "http://purl.obolibrary.org/obo/OBI_0100026";
	public final static String ONT_CLASS_GENE_SOFA = "http://purl.obolibrary.org/obo/SO_0000704";
	
	// SO

	// public final static String ONT_CLASS_GENE_EFO =
	// "http://identifiers.org/so/SO:0000704";

	
	public final static String ONT_CLASS_HOST_EFO = "http://www.ebi.ac.uk/efo/EFO_0000532";
	public final static String ONT_CLASS_DISEASE = "http://www.ebi.ac.uk/efo/EFO_0000408";
	public final static String ONT_CLASS_WILD_TYPE_GENOTYPE = "http://www.ebi.ac.uk/efo/EFO_0005168";
	public final static String ONT_CLASS_PATHOGEN_EFO = "http://www.ebi.ac.uk/efo/EFO_0000643";
	
	// SCHEMA.ORG
	
	public final static String ONT_CLASS_CREATIVE_WORK = "http://schema.org/CreativeWork";
	public final static String ONT_PROP_CITATION = "http://schema.org/citation";
	
	// SIO

	public final static String ONT_CLASS_INVESTIGATION = "http://semanticscience.org/resource/SIO_000747";
	public final static String ONT_CLASS_ORGANISM_SIO = "http://semanticscience.org/resource/SIO_010000";
	public final static String ONT_CLASS_HOST_SIO = "http://semanticscience.org/resource/SIO_010415";
	public final static String ONT_CLASS_DESCRIPTION = "http://semanticscience.org/resource/SIO_000136";
	public final static String ONT_CLASS_ALLELE = "http://semanticscience.org/resource/SIO_010277";
	public final static String ONT_CLASS_GENE_SIO = "http://semanticscience.org/resource/SIO_010035";
	public final static String ONT_CLASS_PATHOGEN_SIO = "http://semanticscience.org/resource/SIO_010414";
	public final static String ONT_CLASS_SCIENTIFIC_NAME = "http://semanticscience.org/resource/SIO_000120";
	public final static String ONT_CLASS_STRAIN = "http://semanticscience.org/resource/SIO_010055";
	public final static String ONT_CLASS_ATTRIBUTE = "http://semanticscience.org/resource/SIO_000614";
	
	public final static String ONT_CLASS_GENE_LETHAL_KNOCKOUT = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00013";
	public final static String ONT_CLASS_GENE_MUTATION_PARAMETER = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00024";
	public final static String ONT_CLASS_HOST_CONTEXT = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00074";
	public final static String ONT_CLASS_INTERACTION_CONTEXT = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00076";
	public final static String ONT_CLASS_HOST_RESPONSE = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00002";
	public final static String ONT_CLASS_PATHOGENIC_INTERACTION = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00022";
	public final static String ONT_CLASS_IN_VITRO_GROWTH = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00018";
	public final static String ONT_CLASS_PHI_BASE_ACCESSION = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00111";
	public final static String ONT_CLASS_PATHOGEN_CONTEXT = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00075";
	public final static String ONT_CLASS_EXPERIMENT_SPECIFICATION = ONT_URI_BASE_SCHEMA
			+ "#ESO_0000000";
	public final static String ONT_CLASS_ASSOCIATED_STRAIN = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00017";
	public final static String ONT_CLASS_DISEASE_NAME = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00001";
	public final static String ONT_CLASS_EXPERIMENTAL_HOST = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00000";
	public final static String ONT_CLASS_FUNCTION = ONT_URI_BASE_SCHEMA
			+ "#PHIBO_00004";

	public final static String ONT_CLASS_CONCEPT_SKOS = "http://www.w3.org/2004/02/skos/core#Concept";

	public final static String ONT_CLASS_ESO = ONT_URI_BASE_SCHEMA
			+ "#ESO_0000000";
	public final static String ONT_CLASS_POO = ONT_URI_BASE_SCHEMA
			+ "#POO_0000000";

	public final static String URI_INTERACTION = "/interaction/";
	public final static String PREFIX_INTERACTION = "INT_";
	public final static String URI_IN_VITRO_GROWTH = "/invitrogrowth/";
	public final static String PREFIX_IN_VITRO_GROWTH = "IVG_";
	public final static String URI_ALLELE = "/allele/";
	public final static String PREFIX_ALLELE = "ALLELE_";
	public final static String URI_GENE = "/gene/";
	public final static String PREFIX_GENE = "GENE_";
	public final static String URI_LOCUS_ID = "/locusid/";
	public final static String PREFIX_LOCUS_ID = "LOCID_";
	public final static String URI_LETHAL_KNOCKOUT = "/lethalknockout/";
	public final static String PREFIX_LETHAL_KNOCKOUT = "LETHKNOCK_";
	public final static String URI_PATHOGEN_WILD_TYPE = "/wildtype/pathogen/";
	public final static String PREFIX_PATHOGEN_WILD_TYPE = "WTPATH_";
	public final static String URI_HOST_WILD_TYPE = "/wildtype/host/";
	public final static String PREFIX_HOST_WILD_TYPE = "WTHOST_";
	public final static String URI_GMP = "/genemutationparameters/";
	public final static String PREFIX_GMP = "GMP_";
	public final static String URI_MUTATION_PHENOTYPE = "/mutationphenotype/";
	public final static String PREFIX_MUTATION_PHENOTYPE = "MUTPHE_";
	public final static String URI_EXPERIMENTAL_EVIDENCE = "/experimentalevidence/";
	public final static String PREFIX_EXPERIMENTAL_EVIDENCE = "EXPEVD_";
	public final static String URI_FUNCTION = "/genefunction/";
	public final static String PREFIX_FUNCTION = "GENEFUNCT_";
	public final static String URI_AA_SEQ = "/aaseq/";
	public final static String PREFIX_AA_SEQ = "AASEQ_";
	public final static String URI_NT_SEQ = "/ntseq/";
	public final static String PREFIX_NT_SEQ = "NTSEQ_";
	public final static String URI_GENE_NAME = "/genename/";
	public final static String PREFIX_GENE_NAME = "GENENAME_";
	public final static String URI_GENE_ACCESSION = "/geneaccession/";
	public final static String PREFIX_GENE_ACCESSION = "GENEACC_";
	public final static String URI_PATHOGEN = "/pathogen/";
	public final static String PREFIX_PATHOGEN = "PATHOGEN_";
	public final static String URI_HOST = "/host/";
	public final static String PREFIX_HOST = "HOST_";
	public final static String URI_PATHOGEN_STRAIN = "/pathogenstrain/";
	public final static String PREFIX_PATHOGEN_STRAIN = "PATHSTRAIN_";
	public final static String URI_PATHOGEN_ASSOCIATED_STRAIN = "/pathogenassociatedstrain/";
	public final static String PREFIX_PATHOGEN_ASSOCIATED_STRAIN = "PATHASSSTRAIN_";
	public final static String URI_PATHOGEN_NCBI_TAXONOMY_ID = "/pathogenncbitaxid/";
	public final static String PREFIX_PATHOGEN_NCBI_TAXONOMY_ID = "PATHNCBITAXID_";
	public final static String URI_HOST_CONTEXT = "/hostcontext/";
	public final static String PREFIX_HOST_CONTEXT = "HOSTCONTEXT_";
	public final static String URI_DESCRIPTION = "/description/";
	public final static String PREFIX_DESCRIPTION = "DESC_";
	public final static String URI_CITATION = "/citation/";
	public final static String PREFIX_CITATION = "CITATION_";
	public final static String URI_PROTOCOL = "/protocol/";
	public final static String PREFIX_PROTOCOL = "PROTOCOL_";
	public final static String URI_INTERACTION_CONTEXT = "/intcontext/";
	public final static String PREFIX_INTERACTION_CONTEXT = "INTCONTEXT_";
	public final static String URI_EXPERIMENTAL_HOST = "/experimentalhost/";
	public final static String PREFIX_EXPERIMENTAL_HOST = "EXPHOST_";
	public final static String URI_HOST_RESPONSE = "/hostresponse/";
	public final static String PREFIX_HOST_RESPONSE = "HOSTRESP_";
	public final static String URI_HOST_SPECIES = "/hostspecies/";
	public final static String PREFIX_HOST_SPECIES = "HOSTSPECIES_";
	public final static String URI_PATHOGEN_SPECIES = "/pathogenspecies/";
	public final static String PREFIX_PATHOGEN_SPECIES = "PATHOGENSPECIES_";
	public final static String URI_PUBMED_ID = "/pubmedid/";
	public final static String PREFIX_PUBMED_ID = "PUBMED_ID_";
	public final static String URI_HOST_NCBI_TAXONOMY_ID = "/hostncbitaxid/";
	public final static String PREFIX_HOST_NCBI_TAXONOMY_ID = "HOSTNCBITAXID_";
	public final static String URI_DISEASE_NAME = "/diseasename/";
	public final static String PREFIX_DISEASE_NAME = "DISNAME_";
	public final static String URI_PROTOCOL_DESCRIPTION = "/description/protocol/";
	public final static String PREFIX_PROTOCOL_DESCRIPTION = "PROTDESC_";
	public final static String URI_PATHOGEN_CONTEXT = "/pathogencontext/";
	public final static String PREFIX_PATHOGEN_CONTEXT = "PATHCON_";
	public final static String URI_PHI_BASE_ID = "/phibaseid/";
	public final static String PREFIX_PHI_BASE_ID = "PHIBAID_";

	public final static String GO_DATABASE = "GO";
	public final static String EC_DATABASE = "EC";

	public final static String URI_BASE_GO_ANNOTATION = "http://purl.obolibrary.org/obo/";
	public final static String PREFIX_GO_ANNOTATION = "GOA_";

	public final static String TAXON_ID = "TAXON ID";
	public final static String INTERACTION = "INTERACTION";
	public final static String INTERACTION_CONTEXT = "INTERACTION_CONTEXT";
	public final static String PHI_BASE_ID = "PHI_BASE_ID";
	public final static String DISEASE_NAME = "DISEASE_NAME";
	public final static String HOST_CONTEXT = "HOST_CONTEXT";
	public final static String DESCRIPTION = "DESCRIPTION";
	public final static String DESCRIPTION_PROTOCOL = "DESCRIPTION PROTOCOL";
	public final static String PROTOCOL = "PROTOCOL";
	public final static String EXPERIMENTAL_HOST = "EXPERIMENTAL_HOST";
	public final static String HOST_NCBI_TAXONOMY_ID = "HOST_NCBI_TAXONOMY_ID";
	public final static String SPECIES = "SPECIES";
	public final static String HOST_RESPONSE = "HOST_RESPONSE";
	public final static String PATHOGEN_CONTEXT = "PATHOGEN_CONTEXT";
	public final static String PATHOGEN_WILD_TYPE = "PATHOGEN WILD TYPE";
	public final static String HOST_WILD_TYPE = "HOST WILD TYPE";
	public final static String ALLELE = "ALLELE";
	public final static String GENE = "GENE";
	public final static String HOST = "HOST";
	public final static String NT_SEQUENCE = "NT_SEQUENCE";
	public final static String AA_SEQUENCE = "AA_SEQUENCE";
	public final static String GENE_NAME = "GENE_NAME";
	public final static String GENE_ACCESSION = "GENE_ACCESSION";
	public final static String GENE_FUNCTION = "GENE_FUNCTION";
	public final static String GENE_GMP = "GENE_GMP";
	public final static String EXPERIMENTAL_EVIDENCES = "EXPERIMENTAL_EVIDENCES";
	public final static String MUTATION_PHENOTYPE = "MUTATION_PHENOTYPE";
	public final static String PATHOGEN = "PATHOGEN";
	public final static String PATHOGEN_NCBI_TAXONOMY_ID = "PATHOGEN_NCBI_TAXONOMY_ID";
	public final static String PATHOGEN_STRAIN = "PATHOGEN_STRAIN";
	public final static String PATHOGEN_ASSOCIATED_STRAIN = "PATHOGEN_ASSOCIATED_STRAIN";
	public final static String LETHAL_KNOCKOUT = "LETHAL_KNOCKOUT";
	public final static String LOCUS_ID = "LOCUS_ID";
	public final static String IN_VITRO_GROWTH = "IN_VITRO_GROWTH";
	public final static String GO_ANNOTATION = "GO_ANNOTATION";
	public final static String MUTANT_PHENOTYPE_BASE_STATE = "BASE STATE";
	public final static String HISTORICAL_OBSERVATION = "HISTORICAL OBSERVATION";
	public final static String CITATION = "CITATION";
	public final static String PUBMED_ID = "PUBMED_ID";
	public final static String ZERO_FILL_FORMAT = "%05d";
	
	public final static String WILD_TYPE_LABEL = "wild type";
	
	public final static String XML_FILE = "XML_FILE";
	public final static String LAST_ENTRY = "LAST_ENTRY";
	
	public final static int ERROR = 0x00001;
	public final static int OK = 0x00002;
	
	/*
	 * XML TAGS
	 */
	
	public final static String XML_TAG_MONOCOT_DICOT = "MONOCOT_DICOT";
	public final static String XML_TAG_PENETRATION_DEFECT = "PENETRATION_DEFECT";
	public final static String XML_TAG_PATHWAY = "PATHWAY";
	public final static String XML_TAG_NT_SEQ = "NT_SEQ";
	public final static String XML_TAG_AA_SEQ = "AA_SEQ";
	public final static String XML_TAG_DISEASE_NAME = "DISEASE_NAME";
	public final static String XML_TAG_FULL_CITATION = "FULL_CITATION";
	public final static String XML_TAG_DATABASE = "DATABASE";
	public final static String XML_TAG_PRE_PENETRATION_DEFECT = "PRE-PENETRATION_DEFECT";
	public final static String XML_TAG_POST_PENETRATION_DEFECT = "POST-PENETRATION_DEFECT";
	public final static String XML_TAG_MANUAL_OR_TEXT= "MANUAL_OR_TEXT";
	public final static String XML_TAG_DOI = "DOI";
	public final static String XML_TAG_NCBI_TAX_ID_PATHOGEN = "NCBI_TAX_ID_PATHOGEN";
	public final static String XML_TAG_STRAIN = "STRAIN";
	public final static String XML_TAG_GO_ANNOTATION = "GO_ANNOTATION";
	public final static String XML_TAG_LITERATURE_ID = "LITERATURE_ID";
	public final static String XML_TAG_AUTHOR_EMAIL = "AUTHOR_EMAIL";
	public final static String XML_TAG_CHEMICAL_ACCESSION = "CHEMICAL_ACCESSION";
	public final static String XML_TAG_HOST_TARGET = "HOST_TARGET";
	public final static String XML_TAG_ASSOCIATED_STRAIN = "ASSOCIATED_STRAIN";
	public final static String XML_TAG_MATING_DEFECT_PRIOR_TO_PENETRATION = "MATING_DEFECT_PRIOR_TO_PENETRATION";
	public final static String XML_TAG_ESSENTIAL_GENE_LETHAL_KNOCKOUT = "ESSENTIAL_GENE_LETHAL_KNOCKOUT";
	public final static String XML_TAG_SPECIES_EXPERT = "SPECIES_EXPERT";
	public final static String XML_TAG_VEGETATIVE_SPORES = "VEGETATIVE_SPORES";
	public final static String XML_TAG_SPORE_GERMINATION = "SPORE_GERMINATION";
	public final static String XML_TAG_PHI_BASE_ACC_NUMBER = "PHI_BASE_ACC_NUMBER";
	public final static String XML_TAG_DB_TYPE = "DB_TYPE";
	public final static String XML_TAG_FUNCTION = "FUNCTION";
	public final static String XML_TAG_GENE_NAME = "GENE_NAME";
	public final static String XML_TAG_HOST_RESPONSE = "HOST_RESPONSE";
	public final static String XML_TAG_PHI_BASE_DATA = "PHI_BASE_DATA";
	public final static String XML_TAG_GENOME_LOCATION = "GENOME_LOCATION";
	public final static String XML_TAG_MULTIPLE_MUTATION = "MULTIPLE_MUTATION";
	public final static String XML_TAG_NCBI_TAX_ID_HOST = "NCBI_TAX_ID_HOST";
	public final static String XML_TAG_ACCESSION = "ACCESSION";
	public final static String XML_TAG_EXPERIMENTAL_HOST = "EXPERIMENTAL_HOST";
	public final static String XML_TAG_COMMENTS = "COMMENTS";
	public final static String XML_TAG_REFERENCE = "REFERENCE";
	public final static String XML_TAG_ENTERED_BY = "ENTERED_BY";
	public final static String XML_TAG_INDUCER = "INDUCER";
	public final static String XML_TAG_PHI_BASE_ENTRY = "PHI_BASE_ENTRY";
	public final static String XML_TAG_LITERATURE_SOURCE = "LITERATURE_SOURCE";
	public final static String XML_TAG_PATHOGEN_SPECIES = "PATHOGEN_SPECIES";
	public final static String XML_TAG_YEAR_PUBLISHED = "YEAR_PUBLISHED";
	public final static String XML_TAG_PHI_BASE_ACCESSION = "PHI_BASE_ACCESSION";
	public final static String XML_TAG_SEXUAL_SPORES = "SEXUAL_SPORES";
	public final static String XML_TAG_PHENOTYPE_OF_MUTANT = "PHENOTYPE_OF_MUTANT";
	public final static String XML_TAG_LOCUS_ID = "LOCUS_ID";
	public final static String XML_TAG_IVG = "IVG";
	public final static String XML_TAG_EXPERIMENTAL_EVIDENCE = "EXPERIMENTAL_EVIDENCE";
	public final static String XML_TAG_IDENTIFIER_TYPE_PROTEIN_ID = "IDENTIFIER_TYPE_PROTEIN_ID";
	public final static String XML_TAG_IDENTIFIER_TYPE_GENE_LOCUS_ID = "IDENTIFIER_TYPE_GENE_LOCUS_ID";
	
	public final static String XML_TAGS_MAP = "phi_data/mappings/phi_xml_tags_";
}
