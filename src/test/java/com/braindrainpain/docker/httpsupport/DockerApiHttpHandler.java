/*
	The MIT License (MIT)

	Copyright (c) 2015 Manuel Kasiske

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/
package com.braindrainpain.docker.httpsupport;

import com.braindrainpain.docker.DockerAPI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;

/**
* @author Manuel Kasiske
*/
public class DockerApiHttpHandler implements HttpHandler
{
	protected static final String DUMMY_JSON_SUCCESS_RESPONSE = "{\"name\":\"pharmacy-service\",\"tags\":[\"3\",\"1\",\"latest\",\"2\"]}";
	protected static final String EMPTY_JSON_RESPONSE = "{}";
	protected static final String DUMMY_TAGS_DOCKER_API_URL = MessageFormat.format(DockerAPI.V2.getUrl(),"http://localhost:5000","pharmacy-service");
	protected static final String DUMMY_ERROR_MESSAGE = "{\"errors\":[{\"code\":\"NAME_UNKNOWN\",\"message\":\"repository name not known to registry\",\"detail\":{\"name\":\"{0}\"}}]}";
	protected static final String DUMMY_MANIFESTS_DOCKER_API_URL = MessageFormat.format(DockerAPI.MANIFEST.getUrl(),"http://localhost:5000","pharmacy-service","latest");

	protected final static String DUMMY_MANIFESTS_JSON_RESPONSE = "{\n" +
		"   \"schemaVersion\": 1,\n" +
		"   \"name\": \"pharmacy-service\",\n" +
		"   \"tag\": \"latest\",\n" +
		"   \"architecture\": \"amd64\",\n" +
		"   \"fsLayers\": [\n" +
		"      {\n" +
		"         \"blobSum\": \"sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"blobSum\": \"sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"blobSum\": \"sha256:9d7588d3c0635b53bd9a7dcd40bdf5d2d32cd3fb919c3a29ec2febbc2449eb19\"\n" +
		"      }\n" +
		"   ],\n" +
		"   \"history\": [\n" +
		"      {\n" +
		"         \"v1Compatibility\": \"{\\\"id\\\":\\\"21047564fb9c509d92641d4202d38642eb353ac1c021925e34850ac213dcda7d\\\",\\\"parent\\\":\\\"d7057cb020844f245031d27b76cb18af05db1cc3a96a29fa7777af75f5ac91a3\\\",\\\"created\\\":\\\"2015-09-25T15:56:46.273986272Z\\\",\\\"container\\\":\\\"57ba4e22c6594a900b48918d7c083c112ed0df639d352aa4c11b0d8055aaa9c8\\\",\\\"container_config\\\":{\\\"Hostname\\\":\\\"5f8e0e129ff1\\\",\\\"Domainname\\\":\\\"\\\",\\\"User\\\":\\\"\\\",\\\"AttachStdin\\\":false,\\\"AttachStdout\\\":false,\\\"AttachStderr\\\":false,\\\"ExposedPorts\\\":null,\\\"PublishService\\\":\\\"\\\",\\\"Tty\\\":false,\\\"OpenStdin\\\":false,\\\"StdinOnce\\\":false,\\\"Env\\\":[\\\"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\\\"],\\\"Cmd\\\":[\\\"/bin/sh\\\",\\\"-c\\\",\\\"#(nop) CMD [\\\\\\\"/bin/sh\\\\\\\" \\\\\\\"-c\\\\\\\" \\\\\\\"echo Hello World\\\\\\\"]\\\"],\\\"Image\\\":\\\"d7057cb020844f245031d27b76cb18af05db1cc3a96a29fa7777af75f5ac91a3\\\",\\\"Volumes\\\":null,\\\"VolumeDriver\\\":\\\"\\\",\\\"WorkingDir\\\":\\\"\\\",\\\"Entrypoint\\\":null,\\\"NetworkDisabled\\\":false,\\\"MacAddress\\\":\\\"\\\",\\\"OnBuild\\\":[],\\\"Labels\\\":null},\\\"docker_version\\\":\\\"1.8.2\\\",\\\"config\\\":{\\\"Hostname\\\":\\\"5f8e0e129ff1\\\",\\\"Domainname\\\":\\\"\\\",\\\"User\\\":\\\"\\\",\\\"AttachStdin\\\":false,\\\"AttachStdout\\\":false,\\\"AttachStderr\\\":false,\\\"ExposedPorts\\\":null,\\\"PublishService\\\":\\\"\\\",\\\"Tty\\\":false,\\\"OpenStdin\\\":false,\\\"StdinOnce\\\":false,\\\"Env\\\":[\\\"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\\\"],\\\"Cmd\\\":[\\\"/bin/sh\\\",\\\"-c\\\",\\\"echo Hello World\\\"],\\\"Image\\\":\\\"d7057cb020844f245031d27b76cb18af05db1cc3a96a29fa7777af75f5ac91a3\\\",\\\"Volumes\\\":null,\\\"VolumeDriver\\\":\\\"\\\",\\\"WorkingDir\\\":\\\"\\\",\\\"Entrypoint\\\":null,\\\"NetworkDisabled\\\":false,\\\"MacAddress\\\":\\\"\\\",\\\"OnBuild\\\":[],\\\"Labels\\\":null},\\\"architecture\\\":\\\"amd64\\\",\\\"os\\\":\\\"linux\\\",\\\"Size\\\":0}\\n\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"v1Compatibility\": \"{\\\"id\\\":\\\"d7057cb020844f245031d27b76cb18af05db1cc3a96a29fa7777af75f5ac91a3\\\",\\\"parent\\\":\\\"cfa753dfea5e68a24366dfba16e6edf573daa447abf65bc11619c1a98a3aff54\\\",\\\"created\\\":\\\"2015-09-21T20:15:47.866196515Z\\\",\\\"container\\\":\\\"7f652467f9e6d1b3bf51172868b9b0c2fa1c711b112f4e987029b1624dd6295f\\\",\\\"container_config\\\":{\\\"Hostname\\\":\\\"5f8e0e129ff1\\\",\\\"Domainname\\\":\\\"\\\",\\\"User\\\":\\\"\\\",\\\"AttachStdin\\\":false,\\\"AttachStdout\\\":false,\\\"AttachStderr\\\":false,\\\"ExposedPorts\\\":null,\\\"PublishService\\\":\\\"\\\",\\\"Tty\\\":false,\\\"OpenStdin\\\":false,\\\"StdinOnce\\\":false,\\\"Env\\\":null,\\\"Cmd\\\":[\\\"/bin/sh\\\",\\\"-c\\\",\\\"#(nop) CMD [\\\\\\\"sh\\\\\\\"]\\\"],\\\"Image\\\":\\\"cfa753dfea5e68a24366dfba16e6edf573daa447abf65bc11619c1a98a3aff54\\\",\\\"Volumes\\\":null,\\\"VolumeDriver\\\":\\\"\\\",\\\"WorkingDir\\\":\\\"\\\",\\\"Entrypoint\\\":null,\\\"NetworkDisabled\\\":false,\\\"MacAddress\\\":\\\"\\\",\\\"OnBuild\\\":null,\\\"Labels\\\":null},\\\"docker_version\\\":\\\"1.8.2\\\",\\\"config\\\":{\\\"Hostname\\\":\\\"5f8e0e129ff1\\\",\\\"Domainname\\\":\\\"\\\",\\\"User\\\":\\\"\\\",\\\"AttachStdin\\\":false,\\\"AttachStdout\\\":false,\\\"AttachStderr\\\":false,\\\"ExposedPorts\\\":null,\\\"PublishService\\\":\\\"\\\",\\\"Tty\\\":false,\\\"OpenStdin\\\":false,\\\"StdinOnce\\\":false,\\\"Env\\\":null,\\\"Cmd\\\":[\\\"sh\\\"],\\\"Image\\\":\\\"cfa753dfea5e68a24366dfba16e6edf573daa447abf65bc11619c1a98a3aff54\\\",\\\"Volumes\\\":null,\\\"VolumeDriver\\\":\\\"\\\",\\\"WorkingDir\\\":\\\"\\\",\\\"Entrypoint\\\":null,\\\"NetworkDisabled\\\":false,\\\"MacAddress\\\":\\\"\\\",\\\"OnBuild\\\":null,\\\"Labels\\\":null},\\\"architecture\\\":\\\"amd64\\\",\\\"os\\\":\\\"linux\\\",\\\"Size\\\":0}\\n\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"v1Compatibility\": \"{\\\"id\\\":\\\"cfa753dfea5e68a24366dfba16e6edf573daa447abf65bc11619c1a98a3aff54\\\",\\\"created\\\":\\\"2015-09-21T20:15:47.433616227Z\\\",\\\"container\\\":\\\"5f8e0e129ff1e03bbb50a8b6ba7636fa5503c695125b1c392490d8aa113e8cf6\\\",\\\"container_config\\\":{\\\"Hostname\\\":\\\"5f8e0e129ff1\\\",\\\"Domainname\\\":\\\"\\\",\\\"User\\\":\\\"\\\",\\\"AttachStdin\\\":false,\\\"AttachStdout\\\":false,\\\"AttachStderr\\\":false,\\\"ExposedPorts\\\":null,\\\"PublishService\\\":\\\"\\\",\\\"Tty\\\":false,\\\"OpenStdin\\\":false,\\\"StdinOnce\\\":false,\\\"Env\\\":null,\\\"Cmd\\\":[\\\"/bin/sh\\\",\\\"-c\\\",\\\"#(nop) ADD file:6cccb5f0a3b3947116a0c0f55d071980d94427ba0d6dad17bc68ead832cc0a8f in /\\\"],\\\"Image\\\":\\\"\\\",\\\"Volumes\\\":null,\\\"VolumeDriver\\\":\\\"\\\",\\\"WorkingDir\\\":\\\"\\\",\\\"Entrypoint\\\":null,\\\"NetworkDisabled\\\":false,\\\"MacAddress\\\":\\\"\\\",\\\"OnBuild\\\":null,\\\"Labels\\\":null},\\\"docker_version\\\":\\\"1.8.2\\\",\\\"config\\\":{\\\"Hostname\\\":\\\"5f8e0e129ff1\\\",\\\"Domainname\\\":\\\"\\\",\\\"User\\\":\\\"\\\",\\\"AttachStdin\\\":false,\\\"AttachStdout\\\":false,\\\"AttachStderr\\\":false,\\\"ExposedPorts\\\":null,\\\"PublishService\\\":\\\"\\\",\\\"Tty\\\":false,\\\"OpenStdin\\\":false,\\\"StdinOnce\\\":false,\\\"Env\\\":null,\\\"Cmd\\\":null,\\\"Image\\\":\\\"\\\",\\\"Volumes\\\":null,\\\"VolumeDriver\\\":\\\"\\\",\\\"WorkingDir\\\":\\\"\\\",\\\"Entrypoint\\\":null,\\\"NetworkDisabled\\\":false,\\\"MacAddress\\\":\\\"\\\",\\\"OnBuild\\\":null,\\\"Labels\\\":null},\\\"architecture\\\":\\\"amd64\\\",\\\"os\\\":\\\"linux\\\",\\\"Size\\\":1095501}\\n\"\n" +
		"      }\n" +
		"   ],\n" +
		"   \"signatures\": [\n" +
		"      {\n" +
		"         \"header\": {\n" +
		"            \"jwk\": {\n" +
		"               \"crv\": \"P-256\",\n" +
		"               \"kid\": \"NJPU:7R5U:X4EV:ZLQH:O43Z:XTXX:NIL3:IMES:HSRG:D22D:73HQ:KXAC\",\n" +
		"               \"kty\": \"EC\",\n" +
		"               \"x\": \"XQ5t4E4zuxeaKIdTotxkvfO9pvAOnqV1ciPhWGz7XcI\",\n" +
		"               \"y\": \"EHQ-lswWTvtrvZhLxiNBNVzOhLdg082GSsxj8EKsL14\"\n" +
		"            },\n" +
		"            \"alg\": \"ES256\"\n" +
		"         },\n" +
		"         \"signature\": \"9QLfCo_fIbNo-49EidYxUIbfGVNSyV_0-Jty8pBOjz89zkj7xQh2ESwtQ7llhYGPiBaC8XuHdmXNA4XYsWl9uA\",\n" +
		"         \"protected\": \"eyJmb3JtYXRMZW5ndGgiOjUwMTksImZvcm1hdFRhaWwiOiJDbjAiLCJ0aW1lIjoiMjAxNS0wOS0yNVQxNjowNDo0NVoifQ\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"header\": {\n" +
		"            \"jwk\": {\n" +
		"               \"crv\": \"P-256\",\n" +
		"               \"kid\": \"NJPU:7R5U:X4EV:ZLQH:O43Z:XTXX:NIL3:IMES:HSRG:D22D:73HQ:KXAC\",\n" +
		"               \"kty\": \"EC\",\n" +
		"               \"x\": \"XQ5t4E4zuxeaKIdTotxkvfO9pvAOnqV1ciPhWGz7XcI\",\n" +
		"               \"y\": \"EHQ-lswWTvtrvZhLxiNBNVzOhLdg082GSsxj8EKsL14\"\n" +
		"            },\n" +
		"            \"alg\": \"ES256\"\n" +
		"         },\n" +
		"         \"signature\": \"_yxde2aHGq25W9zZAFuJ6dy5RdoWEDHm8BVbPzaYjSHQDEfcHWBPXRzW0yoG0UyBez_qbwGX5YCOjrygGvYFBA\",\n" +
		"         \"protected\": \"eyJmb3JtYXRMZW5ndGgiOjUwMTksImZvcm1hdFRhaWwiOiJDbjAiLCJ0aW1lIjoiMjAxNS0wOS0yNVQxNjowMzozMFoifQ\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"header\": {\n" +
		"            \"jwk\": {\n" +
		"               \"crv\": \"P-256\",\n" +
		"               \"kid\": \"NJPU:7R5U:X4EV:ZLQH:O43Z:XTXX:NIL3:IMES:HSRG:D22D:73HQ:KXAC\",\n" +
		"               \"kty\": \"EC\",\n" +
		"               \"x\": \"XQ5t4E4zuxeaKIdTotxkvfO9pvAOnqV1ciPhWGz7XcI\",\n" +
		"               \"y\": \"EHQ-lswWTvtrvZhLxiNBNVzOhLdg082GSsxj8EKsL14\"\n" +
		"            },\n" +
		"            \"alg\": \"ES256\"\n" +
		"         },\n" +
		"         \"signature\": \"hXno4aMkWXeMWlhfAXCn_OWqxktUlJkDktbhLPnikrM63WgjteKHk_Qx33yTghmg0i75-WdqpqjlwkgBj23hug\",\n" +
		"         \"protected\": \"eyJmb3JtYXRMZW5ndGgiOjUwMTksImZvcm1hdFRhaWwiOiJDbjAiLCJ0aW1lIjoiMjAxNS0wOS0yNVQxNTo1Nzo0MVoifQ\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"header\": {\n" +
		"            \"jwk\": {\n" +
		"               \"crv\": \"P-256\",\n" +
		"               \"kid\": \"NJPU:7R5U:X4EV:ZLQH:O43Z:XTXX:NIL3:IMES:HSRG:D22D:73HQ:KXAC\",\n" +
		"               \"kty\": \"EC\",\n" +
		"               \"x\": \"XQ5t4E4zuxeaKIdTotxkvfO9pvAOnqV1ciPhWGz7XcI\",\n" +
		"               \"y\": \"EHQ-lswWTvtrvZhLxiNBNVzOhLdg082GSsxj8EKsL14\"\n" +
		"            },\n" +
		"            \"alg\": \"ES256\"\n" +
		"         },\n" +
		"         \"signature\": \"oS81liGLVMOPQ2j16ETC68habsnsJKDCr6GzUSAc7brHgkBOfLMPXQNymNPDKFrbGdUqp1QZDCMby0za8l7C6Q\",\n" +
		"         \"protected\": \"eyJmb3JtYXRMZW5ndGgiOjUwMTksImZvcm1hdFRhaWwiOiJDbjAiLCJ0aW1lIjoiMjAxNS0wOS0yNVQxNTo1Nzo1NVoifQ\"\n" +
		"      },\n" +
		"      {\n" +
		"         \"header\": {\n" +
		"            \"jwk\": {\n" +
		"               \"crv\": \"P-256\",\n" +
		"               \"kid\": \"NJPU:7R5U:X4EV:ZLQH:O43Z:XTXX:NIL3:IMES:HSRG:D22D:73HQ:KXAC\",\n" +
		"               \"kty\": \"EC\",\n" +
		"               \"x\": \"XQ5t4E4zuxeaKIdTotxkvfO9pvAOnqV1ciPhWGz7XcI\",\n" +
		"               \"y\": \"EHQ-lswWTvtrvZhLxiNBNVzOhLdg082GSsxj8EKsL14\"\n" +
		"            },\n" +
		"            \"alg\": \"ES256\"\n" +
		"         },\n" +
		"         \"signature\": \"oS81liGLVMOPQ2j16ETC68habsnsJKDCr6GzUSAc7brHgkBOfLMPXQNymNPDKFrbGdUqp1QZDCMby0za8l7C6Q\",\n" +
		"         \"protected\": \"eyJmb3JtYXRMZW5ndGgiOjUwMTksImZvcm1hdFRhaWwiOiJDbjAiLCJ0aW1lIjoiMjAxNS0wOS0yNVQxNTo1OTo1MloifQ\"\n" +
		"      }\n" +
		"   ]\n" +
		"}";

	@Override
	public void handle(HttpExchange httpExchange) throws IOException
	{
		final URL tagsUrl = new URL(DUMMY_TAGS_DOCKER_API_URL);
		final URL manifestsUrl = new URL(DUMMY_MANIFESTS_DOCKER_API_URL);

		if (httpExchange.getRequestURI().toString().equals(tagsUrl.getPath()))
		{
			sendMessage(httpExchange, DUMMY_JSON_SUCCESS_RESPONSE);
		}
		else if (httpExchange.getRequestURI().toString().equals(manifestsUrl.getPath()))
		{
			sendMessage(httpExchange, DUMMY_MANIFESTS_JSON_RESPONSE);
		}
		else if (httpExchange.getRequestURI().toString().equals("/v2/"))
		{
			sendMessage(httpExchange, EMPTY_JSON_RESPONSE);
		}
		else if (httpExchange.getRequestURI().toString().matches("^/v2/[a-z]++/tags/list"))
		{
			sendError(httpExchange, DUMMY_ERROR_MESSAGE);
		}
		else
		{
			sendError(httpExchange, "404 page not found");
		}
	}

	/**
	* Sends the given message as JSON-response
	* @param exchange content-type und server header are set
	* @param message message that is sent
	*/
	private void sendMessage(HttpExchange exchange, String message) throws IOException
	{
		exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
		exchange.getResponseHeaders().add("Server", "Mock Server");
		exchange.sendResponseHeaders(200, message.length());
		sendData(message, exchange.getResponseBody());
		exchange.close();
	}

	private void sendError(HttpExchange exchange, String message) throws IOException
	{
		exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
		exchange.getResponseHeaders().add("Server", "Mock Server");
		exchange.sendResponseHeaders(404, message.length());
		sendData(message, exchange.getResponseBody());
		exchange.close();
	}

	/**
	* Sends the given data using the given output stream and closes the stream.
	* @param data data that is transferred
	* @param os output stream that is used for the data transfer
	*/
	private void sendData(String data, OutputStream os) throws IOException
	{
		os.write(data.getBytes("UTF-8") );
		os.flush();
		os.close();
	}
}