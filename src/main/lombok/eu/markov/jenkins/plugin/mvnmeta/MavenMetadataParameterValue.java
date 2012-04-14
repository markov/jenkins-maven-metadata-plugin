/*
 * The MIT License
 *
 * Copyright (c) 2012, AKQA, Georgi "Gesh" Markov
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

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.util.VariableResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.kohsuke.stapler.export.Exported;

/**
 * @author Gesh Markov &lt;gesh@markov.eu&gt;
 * 
 */
@Getter
@Setter
@ToString
public class MavenMetadataParameterValue extends ParameterValue {
  private static final long serialVersionUID = -7853796229856029964L;

  @Exported
  private String            mvnRepoUrl;
  @Exported
  private String            version;

  /**
   * @param name
   */
  protected MavenMetadataParameterValue(String name, String mvnRepoUrl, String version) {
    super(name);
    this.mvnRepoUrl = mvnRepoUrl;
    this.version = version;
  }

  @Override
  public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
    env.put(getName(), getVersion());
  }

  @Override
  public VariableResolver<String> createVariableResolver(AbstractBuild<?, ?> build) {
    return new VariableResolver<String>() {
      public String resolve(String name) {
        return MavenMetadataParameterValue.this.name.equals(name) ? getVersion() : null;
      }
    };
  }
}
