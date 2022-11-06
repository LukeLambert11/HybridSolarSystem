import org.joml.Vector2f;
import org.joml.Vector3f;

import static java.lang.Math.*;
import static java.lang.Math.asin;

public class PentagonalPrism {
    private float[] pentagonalPrismVertices=
            {
                    -.5f, -.683f, -.5f, -.804f, .261f, -.5f, 0.f, .844f, -.5f,      //far left of base
                    -.5f, -.683f, -.5f, 0.f, .844f, -.5f, .5f, -.683f, -.5f,       //far middle of base
                    .5f, -.683f, -.5f, 0.f, .844f, -.5f, .804f, .261f, -.5f,       //far right of base
                    -.5f, -.683f, .5f, -.5f, -.683f, -.5f, .5f, -.683f, -.5f,        //side 1
                    .5f, -.683f, -.5f, .5f, -.683f, .5f, -.5f, -.683f, .5f,         //side 1
                    .5f, -.683f, .5f, .5f, -.683f, -.5f, .804f, .261f, -.5f,        //side 2
                    .804f, .261f, -.5f, .804f, .261f, .5f, .5f, -.683f, .5f,         //side 2
                    .804f, .261f, .5f, .804f, .261f, -.5f, 0.f, .844f, -.5f,         //side 3
                    0.f, .844f, -.5f, 0.f, .844f, .5f, .804f, .261f, .5f,           //side 3
                    0.f, .844f, .5f, 0.f, .844f, -.5f, -.804f, .261f, -.5f,          //side 4
                    -.804f, .261f, -.5f, -.804f, .261f, .5f, 0.f, .844f, .5f,        //side 4
                    -.804f, .261f, .5f, -.804f, .261f, -.5f, -.5f, -.683f, -.5f,    //side 5
                    -.5f, -.683f, -.5f, -.5f, -.683f, .5f, -.804f, .261f, .5f,       //side 5
                    0.f, .844f, .5f, -.804f, .261f, .5f,  -.5f, -.683f, .5f,        //close bottom left of base
                    -.5f, -.683f, 0.5f, .5f, -.683f, 0.5f, 0.f, .844f, .5f,         //close bottom middle of base
                    .5f, -.683f, .5f, .804f, .261f, .5f,  0.f, .844f, .5f           //close bottom right of base

            };
    private float[] pentagonalTextureCoordinates =
            {

                    .2f, 0.f, 0.f,.4f, .5f, 1.f,
                    .2f, 0.f, .5f, 1.f, .8f, 0.f,
                    .8f, 0.f, .5f, 1.f, 1.f, .4f,



                  /*  0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 1.f, 0.f, 1.f, 1.f, 0.f, 1.f,       //different orientation
                    0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 1.f, 0.f, 1.f, 1.f, 0.f, 1.f,
                    0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 1.f, 0.f, 1.f, 1.f, 0.f, 1.f,
                    0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 1.f, 0.f, 1.f, 1.f, 0.f, 1.f,
                    0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 1.f, 0.f, 1.f, 1.f, 0.f, 1.f,*/
                    1.f, 1.f, 0.f, 1.f, 0.f, 0.f, 0.f, 0.f, 1.f, 0.f, 1.f, 1.f,

               //     1.f, 1.f, 0.f, 1.f, 0.f, 0.f, 0.f, 0.f, 1.f, 0.f, 1.f, 1.f,
               //     1.f, 1.f, 0.f, 1.f, 0.f, 0.f, 0.f, 0.f, 1.f, 0.f, 1.f, 1.f,

                    0.f, 0.f, 1.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f, 1.f, 0.f, 0.f,


                    1.f, 1.f, 0.f, 1.f, 0.f, 0.f, 0.f, 0.f, 1.f, 0.f, 1.f, 1.f,
                    1.f, 1.f, 0.f, 1.f, 0.f, 0.f, 0.f, 0.f, 1.f, 0.f, 1.f, 1.f,


                    .5f, 1.f, 0.f,.4f, .2f, 0.f,
                    .2f, 0.f, .8f, 0.f, .5f, 1.f,
                    .8f, 0.f, 1.f, .4f, .5f, 1.f

            };


    PentagonalPrism(){
    }

    public float[] getPentagonalPrismVertices(){
        return pentagonalPrismVertices;
    }
    public float[] getPentagonalTextureCoordinates(){
        return pentagonalTextureCoordinates;
    }
    public int numberOfVertices(){
        return 48;
    }


}