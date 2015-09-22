/*
 * The MIT License
 *
 * Copyright (c) 2015, Silpion IT-Solutions GmbH, Marc Rohlfs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
