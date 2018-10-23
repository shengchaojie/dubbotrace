package com.scj.dubbotrace.filter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Logger logger = LoggerFactory.getLogger(BodyReaderHttpServletRequestWrapper.class);

    private final byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        //强制让tomcat对表单提交请求进行解析
        request.getParameterNames();
        InputStream is = null ;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            is = request.getInputStream();
            int size =0;
            byte[] buffer = new byte[1024];
            while((size=is.read(buffer))>0){
                baos.write(buffer,0,size);
            }
        } catch (IOException e) {
            logger.error("读取reuqestbody内容Io出现异常",e);
        }finally {
            body =baos.toByteArray();
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }



    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
