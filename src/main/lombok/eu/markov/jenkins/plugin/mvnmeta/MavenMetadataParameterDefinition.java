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
import hudson.Util;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.util.FormValidation;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import lombok.Getter;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jvnet.localizer.ResourceBundleHolder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * 
 * 
 * @author Gesh Markov &lt;gesh@markov.eu&gt;
 */
@Getter
public class MavenMetadataParameterDefinition extends ParameterDefinition {
  private static final long    serialVersionUID     = -3820719539319589460L;

  private static final String  DEFAULT_FIRST        = "FIRST";
  private static final String  DEFAULT_LAST         = "LAST";
  private static final String  DEFAULT_LATEST       = "LATEST";
  private static final String  DEFAULT_RELEASE      = "RELEASE";
  private static final Pattern MATCH_ALL            = Pattern.compile(".*");

  private transient int        maxVers              = Integer.MIN_VALUE;
  private transient Pattern    versionFilterPattern = null;                  ;

  private final String         repoBaseUrl;
  private final String         groupId;
  private final String         artifactId;
  private final String         defaultValue;
  private final String         versionFilter;
  private final SortOrder      sortOrder;
  private final String         maxVersions;

  @DataBoundConstructor
  public MavenMetadataParameterDefinition(String name, String description, String repoBaseUrl, String groupId,
      String artifactId, String versionFilter, String sortOrder, String defaultValue, String maxVersions) {
    super(name, description);
    this.repoBaseUrl = Util.removeTrailingSlash(repoBaseUrl);
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.versionFilter = StringUtils.trim(versionFilter);
    this.sortOrder = SortOrder.valueOf(sortOrder);
    this.defaultValue = StringUtils.trim(defaultValue);
    this.maxVersions = maxVersions;
  }

  public int getMaxVers() {
    if (this.maxVers == Integer.MIN_VALUE) {
      int maxVers = Integer.MAX_VALUE;
      try {
        maxVers = Integer.parseInt(maxVersions);
      } catch (NumberFormatException e) {
      }
      this.maxVers = maxVers;
    }
    return this.maxVers;
  }

  public Pattern getVersionFilterPattern() {
    if (versionFilterPattern == null) {
      if (StringUtils.isNotBlank(this.versionFilter)) {
        versionFilterPattern = Pattern.compile(this.versionFilter);
      } else {
        versionFilterPattern = MATCH_ALL;
      }
    }
    return versionFilterPattern;
  }

  // This method is invoked from a GET or POST HTTP request
  @Override
  public ParameterValue createValue(StaplerRequest req) {
    String[] values = req.getParameterValues(getName());
    if (values == null || values.length != 1) {
      return this.getDefaultParameterValue();
    }

    String version = values[0];

    return new MavenMetadataParameterValue(getName(), getDescription(), getGroupId(), getArtifactId(), version,
        getVersionUrl(version));
  }

  // This method is invoked when the user clicks on the "Build" button of Hudon's GUI
  @Override
  public ParameterValue createValue(StaplerRequest req, JSONObject formData) {
    MavenMetadataParameterValue value = req.bindJSON(MavenMetadataParameterValue.class, formData);
    value.setDescription(getDescription());
    value.setVersionUrl(getVersionUrl(value.getVersion()));
    // here, we could validate the parameter value,
    // but it's of no use because if we return null the build still goes on...
    return value;
  }

  @Override
  public ParameterValue getDefaultParameterValue() {
    String defaultVersion = null;
    if (DEFAULT_FIRST.equals(this.defaultValue)) {
      List<String> allVersions = getArtifactMetadata().versioning.versions;
      if (allVersions.size() > 0) {
        defaultVersion = allVersions.get(0);
      }
    } else if (DEFAULT_LAST.equals(this.defaultValue)) {
      List<String> allVersions = getArtifactMetadata().versioning.versions;
      if (allVersions != null && allVersions.size() > 0) {
        defaultVersion = allVersions.get(allVersions.size() - 1);
      }
    } else if (DEFAULT_LATEST.equals(this.defaultValue)) {
      defaultVersion = getArtifactMetadata().versioning.latest;
    } else if (DEFAULT_RELEASE.equals(this.defaultValue)) {
      defaultVersion = getArtifactMetadata().versioning.release;
    } else {
      defaultVersion = this.defaultValue;
    }

    if (StringUtils.isBlank(defaultVersion)) {
      return null;
    }

    return new MavenMetadataParameterValue(getName(), getDescription(), getGroupId(), getArtifactId(), defaultVersion,
        getVersionUrl(defaultVersion));
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  public List<String> getVersions() {
    return getArtifactMetadata().versioning.versions;
  }

  private MavenMetadataVersions getArtifactMetadata() {
    try {
      JAXBContext context = JAXBContext.newInstance(MavenMetadataVersions.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      MavenMetadataVersions metadata = (MavenMetadataVersions) unmarshaller.unmarshal(new URL(
          getVersionUrl("maven-metadata.xml")));

      if (sortOrder == SortOrder.DESC) {
        Collections.reverse(metadata.versioning.versions);
      }
      metadata.versioning.versions = filterVersions(metadata.versioning.versions);

      return metadata;
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Could not parse maven-metadata.xml", e);
      MavenMetadataVersions result = new MavenMetadataVersions();
      result.versioning.versions.add("<" + e.getMessage() + ">");
      return result;
    }
  }

  /**
   * @param all
   * @return
   */
  private List<String> filterVersions(List<String> all) {
    ArrayList<String> filteredList = new ArrayList<String>(all.size());
    for (String version : all) {
      if (getVersionFilterPattern().matcher(version).matches()) {
        filteredList.add(version);
        if (filteredList.size() == getMaxVers()) {
          break;
        }
      }
    }
    return filteredList;
  }

  /**
   * Creates a full URL based on the given version parameter and the fields of this instance.
   * 
   * @param version
   *          the version for which to create the full URL.
   * @return the full URL for the given version.
   */
  private String getVersionUrl(String version) {
    StringBuilder versionBuilder = new StringBuilder(getRepoBaseUrl());
    versionBuilder.append("/").append(getGroupId().replaceAll("\\.", "/"));
    versionBuilder.append("/").append(getArtifactId());
    versionBuilder.append("/").append(version);

    return versionBuilder.toString();
  }

  public static enum SortOrder {
    DESC("Descending"), ASC("Ascending");
    @Getter
    private String displayName;

    private SortOrder(String displayName) {
      this.displayName = displayName;
    }
  }

  @Extension
  public static class DescriptorImpl extends ParameterDescriptor {

    public FormValidation doCheckVersionFilter(@QueryParameter String value) {
      if (StringUtils.isNotBlank(value)) {
        try {
          Pattern.compile(value);
        } catch (PatternSyntaxException pse) {
          FormValidation.error(ResourceBundleHolder.get(MavenMetadataParameterDefinition.class).format("NotValidRegex"));
        }
      }
      return FormValidation.ok();
    }

    public SortOrder[] getSortOrders() {
      return SortOrder.values();
    }

    @Override
    public String getDisplayName() {
      return ResourceBundleHolder.get(MavenMetadataParameterDefinition.class).format("DisplayName");
    }

  }

  private final static Logger LOGGER = Logger.getLogger(MavenMetadataParameterDefinition.class.getName());

}
