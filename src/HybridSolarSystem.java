import java.awt.event.*;
import java.nio.*;
import java.lang.Math;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import org.joml.*;


public class HybridSolarSystem extends JFrame implements GLEventListener, KeyListener {
    private GLCanvas myCanvas;
    private double startTime = 0.0;
    private double elapsedTime;
    private int renderingProgram;
    private int vao[] = new int[1];
    private int vbo[] = new int[12];
    private float cameraX, cameraY, cameraZ;//Position of camera
    private float targetX, targetY, targetZ;//Where camera is looking at
    private float cfX, cfY, cfZ;//front of camera
    private Vector3f camVec, targetVec, cfVec; // f,r,u;//Vectors that define camera position, target, f, r and u
    private float Ddist = 1.0f, Dangle = 0.075f; //Distance and angle increments
    private Sphere mySphere;
    private Torus myTorus;
    private int numSphereVerts;
    private int numTorusVertices, numTorusIndices;
    private int moonTexture;
    private int lukeTexture;
    private int earthTexture;
    private int donutTexture;
    private int tennisTexture;
    private int lollipopTexture;


    //booleans for changing things with keyboard
    Boolean axisView = true; //displaying the axis


    // allocate variables for display() function
    private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
    private Matrix4fStack mvStack = new Matrix4fStack(5);
    private Matrix4f pMat = new Matrix4f(); // perspective matrix
    private Matrix4f vMat = new Matrix4f();  // view matrix
    private int mvLoc, projLoc;
    private int color;
    private int isTexture;
    private float aspect;
    private double tf;

    public HybridSolarSystem() {
        setTitle("Hybrid Solar System");
        setSize(600, 600);
        //Making sure we get a GL4 context for the canvas
        GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
        myCanvas = new GLCanvas(capabilities);
        //end GL4 context
        myCanvas.addGLEventListener(this);
        myCanvas.addKeyListener(this);//Listen for keystrokes
        this.add(myCanvas);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Animator animator = new Animator(myCanvas);
        animator.start();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        elapsedTime = System.currentTimeMillis() - startTime;

        gl.glUseProgram(renderingProgram);
        mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
        projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix");
        color = gl.glGetUniformLocation(renderingProgram, "color");
        isTexture = gl.glGetUniformLocation(renderingProgram, "isTexture");

        aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);


        vMat.identity();

        cfVec.add(camVec, targetVec);

        vMat.lookAt(camVec, targetVec, new Vector3f(0.0f, 1.0f, 0.0f));

        gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));


        // push view matrix onto the stack
        mvStack.pushMatrix();

        //look at view
        mvStack.mul(vMat);

        //display axis'
        gl.glUniform1i(isTexture, 0); //for displaying single color

        if (axisView) {

            //display x-axis
            gl.glUniform3f(color, 1.f, 0.f, 0.f); //red
            gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);
            gl.glDrawArrays(GL_LINES, 0, 36);

            //display y-axis
            gl.glUniform3f(color, 0.f, 1.f, 0.f); //green
            gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);
            gl.glDrawArrays(GL_LINES, 0, 36);

            //display z-axis
            gl.glUniform3f(color, 0.f, 0.f, 1.f); //blue
            gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);
            gl.glDrawArrays(GL_LINES, 0, 36);

        }


        tf = elapsedTime / 1000.0;  // time factor

        // ----------------------  luke sun

        gl.glUniform1i(isTexture, 1); //for displaying textures

        mvStack.pushMatrix();
        mvStack.translate(0.0f, 0.0f, 0.0f);
        mvStack.pushMatrix();
        mvStack.rotate((float) tf, 0.0f, 1.0f, 0.0f);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        //  gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, lukeTexture);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_CULL_FACE_MODE);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0, 48);
        mvStack.popMatrix();

        //-----------------------  1st planet earth
        mvStack.pushMatrix();
        mvStack.rotate((float) 0.7854, 0.0f, 0.0f, 1.0f);
        mvStack.translate((float) Math.sin(tf * 2.13) * 4.0f, 0.0f, (float) Math.cos(tf * 2.13) * 4.0f);
        mvStack.pushMatrix();
        mvStack.rotate((float) tf, 0.0f, 1.0f, 0.0f);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, earthTexture);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);


        mvStack.popMatrix();


        //-----------------------   moon
        mvStack.pushMatrix();
        mvStack.translate(0.0f, (float) Math.sin(tf) * 2.0f, (float) Math.cos(tf) * 2.0f);
        mvStack.rotate((float) tf, 0.0f, 0.0f, 1.0f);
        mvStack.scale(0.4f, 0.4f, 0.4f);


        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, moonTexture);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);


        mvStack.popMatrix(); //pop moon
        mvStack.popMatrix(); //pop earth

        // ---------------------- donut

        mvStack.pushMatrix();
        mvStack.rotate((float) .743, .64f, 0.0f, -1.31f);
        mvStack.translate((float) Math.sin(tf) * 6.0f, 0.0f, (float) Math.cos(tf) * 6.0f);
        mvStack.pushMatrix();
        mvStack.rotate((float) tf / 2, 0.55f, 0.0f, -.64f);


        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, donutTexture);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[11]);
        gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);


        mvStack.popMatrix(); //pop donut rotation

        //-----------------------  around donut

        mvStack.pushMatrix();
        mvStack.translate((float) Math.sin(tf) * 2.33f, 0.f, (float) Math.cos(tf) * 2.33f);
        mvStack.rotate((float) tf, 1.0f, 0.0f, 0.0f);
        mvStack.scale(0.3f, 0.3f, 0.3f);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, lollipopTexture);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);


        mvStack.popMatrix(); //pop lollipop
        mvStack.popMatrix(); //pop donut

        //-----------------------  tennis planet

        mvStack.pushMatrix();
        mvStack.rotate((float) 0.665, 0.76f, 0.6f, 0.f);
        mvStack.translate((float) Math.sin(tf / .31f) * 9f, 0.f, (float) Math.cos(tf / .31f) * 9f);
        mvStack.rotate((float) tf, 1.f, 0.f, 0.f);

        gl.glUniform1i(isTexture, 1);


        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, tennisTexture);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);


        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        startTime = System.currentTimeMillis();
        renderingProgram = Utils.createShaderProgram("HybridSolarSystemData/vertShader.glsl", "HybridSolarSystemData/fragShader.glsl");
        setupVertices();
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 13.0f;
        targetX = 0.0f;
        targetY = 0.0f;
        targetZ = 0.0f;
        cfX = 0.f;
        cfY = 0.0f;
        cfZ = -1.0f;
        camVec = new Vector3f(cameraX, cameraY, cameraZ);
        targetVec = new Vector3f(targetX, targetY, targetZ);
        cfVec = new Vector3f(cfX, cfY, cfZ);

        moonTexture = Utils.loadTexture("HybridSolarSystemData/moon.jpeg");
        lukeTexture = Utils.loadTexture("HybridSolarSystemData/luke.jpeg");
        earthTexture = Utils.loadTexture("HybridSolarSystemData/earth.jpeg");
        tennisTexture = Utils.loadTexture("HybridSolarSystemData/tennis.jpeg");
        donutTexture = Utils.loadTexture("HybridSolarSystemData/donut.jpeg");
        lollipopTexture = Utils.loadTexture("HybridSolarSystemData/lollipop.jpeg");

    }

    private void setupVertices() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        PentagonalPrism pentagonalPrism = new PentagonalPrism();


        float[] xAxisPositions =   // INDEX 2
                {
                        -100.f, 0.f, 0.f, 100.f, 0, 0

                };
        float[] yAxisPositions =        // INDEX 3
                {
                        0.f, -100.f, 0.f, 0.f, 100.f, 0

                };
        float[] zAxisPositions =        // INDEX 4
                {
                        0.f, 0.f, -100.f, 0.f, 0, 100.f

                };

        //sphere            // INDEX 5,6,7
        mySphere = new Sphere(96);
        numSphereVerts = mySphere.getIndices().length;

        int[] indices = mySphere.getIndices();
        Vector3f[] vert = mySphere.getVertices();
        Vector2f[] tex = mySphere.getTexCoords();
        Vector3f[] norm = mySphere.getNormals();

        float[] pvalues = new float[indices.length * 3];
        float[] tvalues = new float[indices.length * 2];
        float[] nvalues = new float[indices.length * 3];

        for (int i = 0; i < indices.length; i++) {
            pvalues[i * 3] = (float) (vert[indices[i]]).x;
            pvalues[i * 3 + 1] = (float) (vert[indices[i]]).y;
            pvalues[i * 3 + 2] = (float) (vert[indices[i]]).z;
            tvalues[i * 2] = (float) (tex[indices[i]]).x;
            tvalues[i * 2 + 1] = (float) (tex[indices[i]]).y;
            nvalues[i * 3] = (float) (norm[indices[i]]).x;
            nvalues[i * 3 + 1] = (float) (norm[indices[i]]).y;
            nvalues[i * 3 + 2] = (float) (norm[indices[i]]).z;
        }
        //torus INDEX 8,9,10,11

        myTorus = new Torus(0.5f, 0.2f, 48);
        numTorusVertices = myTorus.getNumVertices();
        numTorusIndices = myTorus.getNumIndices();

        Vector3f[] tvertices = myTorus.getVertices();
        Vector2f[] tTexCoords = myTorus.getTexCoords();
        Vector3f[] tNormals = myTorus.getNormals();
        int[] tI = myTorus.getIndices();

        float[] p = new float[tvertices.length * 3];
        float[] t = new float[tTexCoords.length * 2];
        float[] n = new float[tNormals.length * 3];

        for (int i = 0; i < numTorusVertices; i++) {
            p[i * 3] = (float) tvertices[i].x();
            p[i * 3 + 1] = (float) tvertices[i].y();
            p[i * 3 + 2] = (float) tvertices[i].z();
            t[i * 2] = (float) tTexCoords[i].x();
            t[i * 2 + 1] = (float) tTexCoords[i].y();
            n[i * 3] = (float) tNormals[i].x();
            n[i * 3 + 1] = (float) tNormals[i].y();
            n[i * 3 + 2] = (float) tNormals[i].z();
        }


        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer prismBuf = Buffers.newDirectFloatBuffer(pentagonalPrism.getPentagonalPrismVertices());
        gl.glBufferData(GL_ARRAY_BUFFER, prismBuf.limit() * 4, prismBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer prismTexBuf = Buffers.newDirectFloatBuffer(pentagonalPrism.getPentagonalTextureCoordinates());
        gl.glBufferData(GL_ARRAY_BUFFER, prismTexBuf.limit() * 4, prismTexBuf, GL_STATIC_DRAW);


        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer xBuf = Buffers.newDirectFloatBuffer(xAxisPositions);
        gl.glBufferData(GL_ARRAY_BUFFER, xBuf.limit() * 4, xBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer yBuf = Buffers.newDirectFloatBuffer(yAxisPositions);
        gl.glBufferData(GL_ARRAY_BUFFER, yBuf.limit() * 4, yBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        FloatBuffer zBuf = Buffers.newDirectFloatBuffer(zAxisPositions);
        gl.glBufferData(GL_ARRAY_BUFFER, zBuf.limit() * 4, zBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
        gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
        gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        FloatBuffer tVertBuf = Buffers.newDirectFloatBuffer(p);
        gl.glBufferData(GL_ARRAY_BUFFER, tVertBuf.limit() * 4, tVertBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
        FloatBuffer tTexBuf = Buffers.newDirectFloatBuffer(t);
        gl.glBufferData(GL_ARRAY_BUFFER, tTexBuf.limit() * 4, tTexBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
        FloatBuffer tNorBuf = Buffers.newDirectFloatBuffer(n);
        gl.glBufferData(GL_ARRAY_BUFFER, tNorBuf.limit() * 4, tNorBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[11]);
        IntBuffer tIdxBuf = Buffers.newDirectIntBuffer(tI);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, tIdxBuf.limit() * 4, tIdxBuf, GL_STATIC_DRAW);

    }

    public static void main(String[] args) {
        new HybridSolarSystem();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println("keyPressed event: "+e);
        //Calculate camera forward, left and up vector
        // r, u, f
        // u, v, n
        // x, y, z


        Vector3f n = new Vector3f();
        camVec.sub(targetVec, n);
        n.normalize();
        Vector3f u = new Vector3f();
        n.negate(u);
        u.cross(0, 1, 0);
        Vector3f v = new Vector3f();
        n.cross(u, v);
        System.out.println(e);

        if (e.getKeyChar() == 'w') {//Move camera forward by Ddist
            camVec.sub(n.mul(Ddist));
            myCanvas.display();
        }
        if (e.getKeyChar() == 's') {//Move camera backwards by Ddist
            camVec.add(n.mul(Ddist));
            myCanvas.display();
        }
        if (e.getKeyChar() == 'd') {//Right
            camVec.add(u.mul(Ddist));
            myCanvas.display();
        }
        if (e.getKeyChar() == 'a') {//Left
            camVec.sub(u.mul(Ddist));
            myCanvas.display();
        }
        if (e.getKeyChar() == 'q') {//Up
            camVec.add(v.mul(Ddist));
            myCanvas.display();
        }
        if (e.getKeyChar() == 'e') {//Down
            camVec.sub(v.mul(Ddist));
            myCanvas.display();
        }


        if (e.getKeyChar() == 'f') {    //return camera to start
            camVec = new Vector3f(0f, 0f, 13.f);
            cfVec = new Vector3f(0f, 0f, -1.f);
            myCanvas.display();
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {     //display the axis activated by space bar
            axisView = !axisView;
            myCanvas.display();
        }
        if (e.getKeyCode() == 38) { //pan down

            cfVec.y += Dangle;
            myCanvas.display();
        }
        if (e.getKeyCode() == 40) { //pan up
            cfVec.y -= Dangle;
            myCanvas.display();
        }
        if (e.getKeyCode() == 37) { //look left
            cfVec.x -= Dangle;
            myCanvas.display();
        }
        if (e.getKeyCode() == 39) { //look right
            cfVec.x += Dangle;
            myCanvas.display();
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
