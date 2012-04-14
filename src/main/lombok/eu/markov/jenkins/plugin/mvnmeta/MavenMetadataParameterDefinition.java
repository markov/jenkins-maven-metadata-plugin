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

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import lombok.Getter;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jvnet.localizer.ResourceBundleHolder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

/**
 * Sample {@link Builder}.
 * 
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link MavenMetadataParameterDefinition} is created. The created instance is
 * persisted to the project configuration XML by using XStream, so this allows
 * you to use instance fields (like {@link #name}) to remember the
 * configuration.
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 * 
 * @author Kohsuke Kawaguchi
 */
public class MavenMetadataParameterDefinition extends ParameterDefinition {

  private static final long         serialVersionUID = 8870104917672751538L;
  private static final String       SORT_ASC         = "ASC";
  private static final String       SORT_DESC        = "DESC";
  private static final List<String> SORT_ORDERS      = Arrays.asList(SORT_DESC, SORT_ASC);

  @Getter
  private final String              tagsDir;
  @Getter
  private final String              versionFilter;
  @Getter
  private final String              sortOrder;
  @Getter
  private final String              defaultValue;
  @Getter
  private final String              maxVersions;

  @DataBoundConstructor
  public MavenMetadataParameterDefinition(String name, String description, String tagsDir, String versionFilter, String sortOrder,
      String defaultValue, String maxVersions) {
    super(name, description);
    this.tagsDir = Util.removeTrailingSlash(tagsDir);
    this.versionFilter = versionFilter;
    this.sortOrder = sortOrder;
    this.defaultValue = defaultValue;
    this.maxVersions = maxVersions;
  }

  // This method is invoked from a GET or POST HTTP request
  @Override
  public ParameterValue createValue(StaplerRequest req) {
    String[] values = req.getParameterValues(getName());
    if (values == null || values.length != 1) {
      return this.getDefaultParameterValue();
    } else {
      return new MavenMetadataParameterValue(getName(), getTagsDir(), values[0]);
    }
  }

  // This method is invoked when the user clicks on the "Build" button of
  // Hudon's GUI
  @Override
  public ParameterValue createValue(StaplerRequest req, JSONObject formData) {
    MavenMetadataParameterValue value = req.bindJSON(MavenMetadataParameterValue.class, formData);
    value.setMvnRepoUrl(getTagsDir());
    // here, we could have checked for the value of the "tag" attribute of the
    // parameter value, but it's of no use because if we return null the build
    // still goes on...
    return value;
  }

  @Override
  public ParameterValue getDefaultParameterValue() {
    if (StringUtils.isEmpty(this.defaultValue)) {
      return null;
    }
    return new MavenMetadataParameterValue(getName(), getTagsDir(), this.defaultValue);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  /**
   * Returns a list of Subversion dirs to be displayed in
   * {@code ListSubversionTagsParameterDefinition/index.jelly}.
   * 
   * <p>
   * This method plainly reuses settings that must have been previously defined
   * when configuring the Subversion SCM.
   * </p>
   * 
   * <p>
   * This method never returns {@code null}. In case an error happens, the
   * returned list contains an error message surrounded by &lt; and &gt;.
   * </p>
   */
  public List<String> getTags() {
    AbstractProject context = null;
    List<AbstractProject> jobs = Hudson.getInstance().getItems(AbstractProject.class);

    List<String> dirs = new ArrayList<String>();

    return dirs;
  }

  /**
   * Removes the parent directory (that is, the tags directory) from a list of
   * directories.
   */
  protected void removeParentDir(List<String> dirs) {
    List<String> dirsToRemove = new ArrayList<String>();
    for (String dir : dirs) {
      if (getTagsDir().endsWith(dir)) {
        dirsToRemove.add(dir);
      }
    }
    dirs.removeAll(dirsToRemove);
  }

  @Extension
  public static class DescriptorImpl extends ParameterDescriptor {

    public FormValidation doCheckVersionFilter(@QueryParameter String value) {
      if (value != null && value.length() == 0) {
        try {
          Pattern.compile(value);
        } catch (PatternSyntaxException pse) {
          FormValidation.error(ResourceBundleHolder.get(MavenMetadataParameterDefinition.class).format("NotValidRegex"));
        }
      }
      return FormValidation.ok();
    }

    public List<String> getSortOrders() {
      return SORT_ORDERS;
    }

    @Override
    public String getDisplayName() {
      return ResourceBundleHolder.get(MavenMetadataParameterDefinition.class).format("DisplayName");
    }

  }

  private final static Logger LOGGER = Logger.getLogger(MavenMetadataParameterDefinition.class.getName());

}
