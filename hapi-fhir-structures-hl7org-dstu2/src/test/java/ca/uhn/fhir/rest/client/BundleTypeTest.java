package ca.uhn.fhir.rest.client;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.hamcrest.Matchers;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.rest.server.Constants;

public class BundleTypeTest {

	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(BundleTypeTest.class);
	private FhirContext ourCtx;
	private HttpClient ourHttpClient;

	private HttpResponse ourHttpResponse;

	@Before
	public void before() {
		ourCtx = FhirContext.forDstu2Hl7Org();

		ourHttpClient = mock(HttpClient.class, new ReturnsDeepStubs());
		ourCtx.getRestfulClientFactory().setHttpClient(ourHttpClient);
		ourCtx.getRestfulClientFactory().setServerValidationModeEnum(ServerValidationModeEnum.NEVER);

		ourHttpResponse = mock(HttpResponse.class, new ReturnsDeepStubs());
	}

	@Test
	public void testTransaction() throws Exception {
		String retVal = ourCtx.newXmlParser().encodeBundleToString(new Bundle());

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(ourHttpClient.execute(capt.capture())).thenReturn(ourHttpResponse);
		when(ourHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(ourHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_ATOM_XML + "; charset=UTF-8"));
		when(ourHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(retVal), Charset.forName("UTF-8")));

		Patient p1 = new Patient();
		p1.addIdentifier().setSystem("urn:system").setValue("value");

		IGenericClient client = ourCtx.newRestfulGenericClient("http://foo");
		client.transaction().withResources(Arrays.asList((IBaseResource) p1)).execute();

		HttpUriRequest value = capt.getValue();

		assertTrue("Expected request of type POST on long params list", value instanceof HttpPost);
		HttpPost post = (HttpPost) value;
		String body = IOUtils.toString(post.getEntity().getContent());
		IOUtils.closeQuietly(post.getEntity().getContent());
		ourLog.info(body);

		assertThat(body, Matchers.containsString("<type value=\"" + BundleTypeEnum.TRANSACTION.getCode()));
	}
}
