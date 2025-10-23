package org.ichwan.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.ichwan.service.impl.ClassRoomServiceImpl;

@Path("/class")
@Produces("application/json")
@Consumes("application/json")
public class ClassRoomResource {

    @Inject
    ClassRoomServiceImpl classRoomService;


}
