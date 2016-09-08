package ml.rugal.sshcommon.springmvc.controller;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * This is the fundamental exception and HTTP error code mapping class.
 *
 * @author Rugal Bernstein
 */
@Slf4j
@ControllerAdvice
@Controller
public class BaseExceptionAction
{

    private static final String BAD_REQUEST = "400 (Bad Request)";

    private static final String NOT_FOUND = "404 (Not Found)";

    private static final String METHOD_NOT_ALLOWED = "405 (Method Not Allowed)";

    private static final String NOT_ACCEPTABLE = "406 (Not Acceptable)";

    private static final String UNSUPPORTED_MEDIA_TYPE = "415 (Unsupported Media Type)";

    private static final String INTERNAL_SERVER_ERROR = "500 (Internal Server Error)";

    /**
     *
     * This method is to address no handler request, throw exception into 404 exception advisor
     *
     * @param request The request that threw exception
     *
     * @throws NoSuchRequestHandlingMethodException Throw this exception so to handle exception error to 404 exception
     *                                              handler.
     */
    @RequestMapping("/**")
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void PathNotFoundHandler(HttpServletRequest request) throws NoSuchRequestHandlingMethodException
    {
        LOG.warn(String.format("{%s occured, request URL: %s, request host: %s",
                               NOT_FOUND, request.getRequestURI(), request.getRemoteAddr()));
        throw new NoSuchRequestHandlingMethodException(request);
    }

    /**
     * Mapping for 400
     *
     * @param req The request that threw exception
     * @param e   Bad exception that was thrown in.
     *
     *
     */
    @ResponseBody
    @ExceptionHandler(
        {
            BindException.class, HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class, TypeMismatchException.class
        })
    public void badRequest(HttpServletRequest req, Exception e)
    {
        LOG.error(BAD_REQUEST, e);
    }

    /**
     * Mapping for 404
     *
     * @param req The request that threw exception
     * @param e   Bad exception that was thrown in.
     *
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        {
            NoHandlerFoundException.class, NoSuchRequestHandlingMethodException.class
        })
    public void notFound(HttpServletRequest req, Exception e)
    {
        LOG.warn(NOT_FOUND);
    }

    /**
     * Mapping for 405
     *
     * @param req The request that threw exception
     * @param e   Bad exception that was thrown in.
     *
     */
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void methodNotAllowed(HttpServletRequest req, Exception e)
    {
        LOG.error(METHOD_NOT_ALLOWED, e);
    }

    /**
     * Mapping for 406
     *
     * @param req The request that threw exception
     * @param e   Bad exception that was thrown in.
     *
     */
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public void notAcceptable(HttpServletRequest req, Exception e)
    {
        LOG.error(NOT_ACCEPTABLE, e);
    }

    /**
     * Mapping for 415
     *
     * @param req The request that threw exception
     * @param e   Bad exception that was thrown in.
     *
     */
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public void unsupportedMediaType(HttpServletRequest req, Exception e)
    {
        LOG.error(UNSUPPORTED_MEDIA_TYPE, e);
    }

    /**
     * Mapping for 500 and all other exceptions.
     *
     * @param req The request that threw exception
     * @param e   Bad exception that was thrown in.
     *
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void internalServerError(HttpServletRequest req, Exception e)
    {
        LOG.error(INTERNAL_SERVER_ERROR, e);
    }
}
