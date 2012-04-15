= DESCRIPTION:

This plugin adds a new build parameter type - maven-metadata. If you select this type and give in a repository URL,
groupId and artifactId the plugin will check for that artifact at the given repository and let whoever started the
build select a version of the artifact to bind as a build parameter.

There are also several options to provide default values that will also get evaluated at run-time:
 * FIRST - will evaluate to the first item in the drop-down that would have been presented had the build been executed manually
 * LAST - will evaluate to the last item in the drop-down that would have been presented had the build been executed manually.
 * RELEASE - will evaluate to the version marked as RELEASE in the [repository metadata][] for the configured artifact.
   The versionFilter even if defined is ignored for this default value.
 * LATEST - will evaluate to the version marked as LATEST in the [repository metadata][] for the configured artifact.
   The versionFilter even if defined is ignored for this default value.

[repository metadata]: http://docs.codehaus.org/display/MAVEN/Repository+Metadata

= REQUIREMENTS:

== Jenkins:

Built and tested against Jenkins 1.447.

= LICENSE & AUTHOR:

Author:: Georgi "Gesh" Markov (<gesh@markov.eu>)

Copyright:: 2012, AKQA, Georgi "Gesh" Markov

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
