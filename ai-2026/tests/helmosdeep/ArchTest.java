package helmosdeep;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Valide les dépendances entre les paquetages.
 */
@SuiteDisplayName("Validation de l'architecture")
public class ArchTest {
	private static final String PROJECT = ArchTest.class.getPackageName();

	@Test
	@DisplayName("Le domaine peut dépendre de util et du domaine")
	void domains_types_are_indenpendant() {
		var classes = new ClassFileImporter().importPackages(PROJECT);

		ArchRule packageAccessRule = classes().that().resideInAPackage(PROJECT + ".domains..").should()
				.onlyAccessClassesThat().resideInAnyPackage(PROJECT + ".domains..", PROJECT + ".util..", "java.lang..",
						"java.math..", "java.time..", "java.util..");

		packageAccessRule.check(classes);
	}

	@Test
	@DisplayName("Les classes utilitaires se suffisent à elle-même")
	void utils_types_are_indenpendant() {
		var classes = new ClassFileImporter().importPackages(PROJECT);

		var packageAccess = classes().that().resideInAPackage(PROJECT + ".util..").should().onlyAccessClassesThat()
				.resideInAnyPackage(PROJECT + ".util..", "java.lang..", "java.math..", "java.time..", "java.util..",
						"java.io..", "java.nio..");

		var allowedClassed = classes().that().resideInAPackage(PROJECT + ".util..").should()
				.haveSimpleName("TxtFileReader").orShould().haveSimpleName("Contract");

		packageAccess.check(classes);
		allowedClassed.check(classes);
	}

	@Test
	@DisplayName("Les superviseurs dépendent du domaine")
	void supervisors_depend_only_on_domains_and_utils() {
		var classes = new ClassFileImporter().importPackages(PROJECT);

		ArchRule myRule = classes().that().resideInAPackage(PROJECT + ".supervisors..").should().onlyAccessClassesThat()
				.resideInAnyPackage(PROJECT + ".domains..", PROJECT + ".supervisors..", "java.awt..", "java.lang..",
						"java.math..", "java.time..", "java.util..")
				.orShould().onlyAccessClassesThat().haveNameMatching(PROJECT + ".util.Contract");

		myRule.check(classes);
	}

	@Test
	void views_depend_only_on_supervisors() {
		var classes = new ClassFileImporter().importPackages(PROJECT);

		ArchRule myRule = classes().that().resideInAPackage(PROJECT + ".views..").should().onlyAccessClassesThat()
				.resideInAnyPackage(PROJECT, PROJECT + ".views..", PROJECT + ".supervisors..", "ai.engine..", "java..",
						"javax..");

		myRule.check(classes);
	}
}
