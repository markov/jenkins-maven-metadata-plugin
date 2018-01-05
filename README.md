# Maven Metadata Plugin

This plugin adds a new build parameter _List maven artifact versions_. If you select this type and enter a `repositoryURL`,
`groupId` and `artifactId` the plugin will check for that artifact at the given repository and let whoever started the
build select a version of the artifact to bind as a build parameter.

There are also several options to provide default values which will also be evaluated at run-time:
- `FIRST` - will evaluate to the first item in the drop-down that would have been presented had the build been executed manually
- `LAST` - will evaluate to the last item in the drop-down that would have been presented had the build been executed manually.
- `RELEASE` - will evaluate to the version marked as `release` in the _maven-metadata.xml_ for the configured artifact.
  The versionFilter even if defined is ignored for this default value.
- `LATEST` - will evaluate to the version marked as `latest` in the _maven-metadata.xml_  for the configured artifact.
  The versionFilter even if defined is ignored for this default value.

This plugin does not download anything but rather sets build environment variables describing the artifact and
where it can be downloaded from.

If all you need to do is download an artifact from a repository and use it during your build you can consider using the
[Repository Connector Plugin](https://wiki.jenkins.io/display/JENKINS/Repository+Connector+Plugin) plugin instead.

## Development

### Requirements

Built and tested against Jenkins 2.32.1.

### Development workspace

When starting the development Jenkins using `mvn hpi:run` and the _work_ directory containg the `JENKINS_HOME` doesn't
already exist, the workspace will be created with some pre-configured test jobs for the plugin.

# Authors & Contributors

Author:
- Georgi "Gesh" Markov [@markov](https://github.com/markov)

Further maintainer(s):
- Marc Rohlfs [@marcrohlfs](https://github.com/marcrohlfs)

Contributors:
- Benjamin Bonnet [@bonnetb](https://github.com/bonnetb)
- David Portabella [@dportabella](https://github.com/dportabella)
- Dominik Bartholdi [@imod](https://github.com/imod)
- Mathieu Pousse [@mathieu-pousse](https://github.com/mathieu-pousse)
- Mads Mohr Christensen [@hrmohr](https://github.com/hrmohr)
- Nandor Galambosi [@dromie](https://github.com/dromie)
- Robert Kleinschmager [@barclay-reg](https://github.com/barclay-reg)

# References

- [Repository Metadata]( http://docs.codehaus.org/display/MAVEN/Repository+Metadata)
- [Repository Connector](: )https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin)
- [Maven Metadata](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Metadata+Plugin)

# Copyright & License

Copyright:: 2012-2014, Georgi "Gesh" Markov

Licensed under the MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
