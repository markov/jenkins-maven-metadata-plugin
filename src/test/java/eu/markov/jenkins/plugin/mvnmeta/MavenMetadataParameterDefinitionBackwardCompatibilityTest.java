package eu.markov.jenkins.plugin.mvnmeta;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import hudson.model.FreeStyleProject;
import hudson.model.ParametersDefinitionProperty;

import java.io.InputStream;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Tests the class {@link MavenMetadataParameterDefinitionBackwardCompatibility}.
 *
 * @author Marc Rohlfs, Silpion IT-Solutions GmbH - <a href="mailto:rohlfs@silpion.de">rohlfs@silpion.de</a>
 */
@SuppressWarnings("deprecation")
public class MavenMetadataParameterDefinitionBackwardCompatibilityTest {

  @Rule
  public JenkinsRule jenkinsRule = new JenkinsRule();

  @Test
  public void testReadResolve() throws Exception {

    // Create the test project from the provided XML file. The migration will implicitly run when the project is created.
    InputStream projectXmlFile = this.getClass().getResourceAsStream("test-migration.xml");
    FreeStyleProject testProject = (FreeStyleProject) this.jenkinsRule.jenkins.createProjectFromXML("test-migration", projectXmlFile);

    // Get the migrated build parameter.
    ParametersDefinitionProperty buildParameters = testProject.getProperty(ParametersDefinitionProperty.class);
    MavenMetadataParameterDefinition mavenMetadataBuildParameter =
        (MavenMetadataParameterDefinition) buildParameters.getParameterDefinition("ARTIFACT");

    // Verify the migration of username and password to a generated credentials object.
    UsernamePasswordCredentials generatedCredentials = mavenMetadataBuildParameter.findCredentialsByCredentialsId();
    assertNotNull(mavenMetadataBuildParameter.getCredentialsId());
    assertEquals(generatedCredentials.getUsername(), mavenMetadataBuildParameter.getUsername());
    assertEquals(generatedCredentials.getPassword().getPlainText(), mavenMetadataBuildParameter.getPassword());
  }
}
