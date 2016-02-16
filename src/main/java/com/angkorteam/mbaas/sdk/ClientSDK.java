package com.angkorteam.mbaas.sdk;

import com.angkorteam.mbaas.request.*;
import com.angkorteam.mbaas.response.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public interface ClientSDK {

    @POST("/security/login")
    public Response login(@Body SecurityLoginRequest request);

    @POST("/security/signup")
    public Response signUp(@Body SecuritySignUpRequest request);

    @POST("/document/create/{collection}")
    public Response createDocument(@Header("X-MBAAS-SESSION") String session, @Path("collection") String collection, @Body DocumentCreateRequest request);

    @POST("/collection/create")
    public Response createCollection(@Header("X-MBAAS-SESSION") String session, @Body CollectionCreateRequest request);

    @POST("/collection/delete")
    public Response deleteCollection(@Header("X-MBAAS-SESSION") String session, @Body CollectionDeleteRequest request);

    @POST("/collection/attribute/create")
    public Response createCollectionAttribute(@Header("X-MBAAS-SESSION") String session, @Body CollectionAttributeCreateRequest request);

    @POST("/collection/attribute/delete")
    public Response deleteCollectionAttribute(@Header("X-MBAAS-SESSION") String session, @Body CollectionAttributeDeleteRequest request);

}
