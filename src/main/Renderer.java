package main;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import transforms.*;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgramViewer, shaderProgramLight, shaderProgramLightPosition;
    private int locView, locProjection, locType, locTime, locLightPosition, locLightVP;
    private int locViewLight, locProjectionLight, locTypeLight, locTimeLight;
    private int locViewLightPosition, locProjectionLightPosition, locLightLightPosition;

    private OGLBuffers buffers;
    private Camera camera, cameraLight;
    private Mat4 projection;
    private OGLTexture2D textureMosaic;
    private OGLTexture2D.Viewer viewer;
    private OGLRenderTarget renderTarget;

    private float time = 0;
    private int gridGranularity, projectionType, shaderType;
    private boolean showObject1, showObject2, showObject3, showObject4, showObject5, showObject6;

    /*ovládání aplikace je realizované pomocí proměných nastavovaných konstruktorem rendereru*/
    public Renderer(int gridGranularity, int projectionType, int shaderType, boolean showObject1, boolean showObject2, boolean showObject3, boolean showObject4, boolean showObject5, boolean showObject6) {

        this.gridGranularity = gridGranularity;
        this.projectionType = projectionType;
        this.shaderType = shaderType;
        this.showObject1 = showObject1;
        this.showObject2 = showObject2;
        this.showObject3 = showObject3;
        this.showObject4 = showObject4;
        this.showObject5 = showObject5;
        this.showObject6 = showObject6;
    }

    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        // Set the clear color
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
        textRenderer = new OGLTextRenderer(width, height);
        glEnable(GL_DEPTH_TEST); // zapne z-test (z-buffer) - až po new OGLTextRenderer
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // vyplnění přivrácených i odvrácených stran

        //výber typu shaderu pro různé druhy osvětlení
        switch (shaderType) {
            case 0:
                //osvětlení per pixel
                shaderProgramViewer = ShaderUtils.loadProgram("/startPerPixel");
                break;
            case 1:
                //osvětlení per vertex
                shaderProgramViewer = ShaderUtils.loadProgram("/startPerVertex");
                break;
        }
        ;
        //shader pro metodu shadow maps
        shaderProgramLight = ShaderUtils.loadProgram("/light");
        //shader pro znázornění pozice světla
        shaderProgramLightPosition = ShaderUtils.loadProgram("/lightPosition");

        //nastavení locátorů pro shaderProgramViewer
        locView = glGetUniformLocation(shaderProgramViewer, "view");
        locProjection = glGetUniformLocation(shaderProgramViewer, "projection");
        locType = glGetUniformLocation(shaderProgramViewer, "type");
        locTime = glGetUniformLocation(shaderProgramViewer, "time");
        locLightPosition = glGetUniformLocation(shaderProgramViewer, "lightPosition");
        locLightVP = glGetUniformLocation(shaderProgramViewer, "lightViewProjection");

        //nastavení locátorů pro shaderProgramLight
        locViewLight = glGetUniformLocation(shaderProgramLight, "view");
        locProjectionLight = glGetUniformLocation(shaderProgramLight, "projection");
        locTypeLight = glGetUniformLocation(shaderProgramLight, "type");
        locTimeLight = glGetUniformLocation(shaderProgramLight, "time");

        //nastavení locátorů pro shaderProgramLightPosition
        locViewLightPosition = glGetUniformLocation(shaderProgramLightPosition, "view");
        locProjectionLightPosition = glGetUniformLocation(shaderProgramLightPosition, "projection");
        locLightLightPosition = glGetUniformLocation(shaderProgramLightPosition, "lightPosition");

        //rendertarget
        renderTarget = new OGLRenderTarget(gridGranularity, gridGranularity);
        //vytvoření gridu
        buffers = GridFactory.generateGrid(gridGranularity, gridGranularity);

        //nastavení pozorovatele
        cameraLight = new Camera()
                .withPosition(new Vec3D(6, 6, 6))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        //nastavení zdroje světla
        camera = new Camera()
                .withPosition(new Vec3D(6, 6, 5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        //výber typu projekce
        switch (projectionType) {
            case 0:
                //perspectivní projekce
                projection = new Mat4PerspRH(
                        Math.PI / 3,
                        LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH,
                        1, // 0.1
                        20 // 50
                );
                break;
            case 1:
                //orthogonální projekce
                projection = new Mat4OrthoRH(20, 20, 1, 20);
                break;
        }
        ;

        //načtení textury
        viewer = new OGLTexture2D.Viewer();
        try {
            textureMosaic = new OGLTexture2D("./textures/bricks.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {

        //používáme k modifikaci těles a zdroje světla
        time += 0.1;

        // zapnout z-test (kvůli textRenderer)
        glEnable(GL_DEPTH_TEST);

        //znázorňuje zdroj světla
        renderLightPosition();
        //uloží z-buffer pro metodu ShadowMaps
        renderFromLight();
        //vyrenderuje scénu
        renderFromViewer();

        viewer.view(renderTarget.getColorTexture(), -1, 0, 0.5);
        viewer.view(renderTarget.getDepthTexture(), -1, -0.5, 0.5);
        viewer.view(textureMosaic, -1, -1, 0.5);
    }

    private void renderLightPosition() {
        //posunujeme kamerou tam a nazpátek
        cameraLight = cameraLight.up(0.05 * Math.sin(0.1 * time));
        glUseProgram(shaderProgramLightPosition);
        glUniform3fv(locLightLightPosition, ToFloatArray.convert(cameraLight.getPosition()));
        //vykreslujeme pozici kamery žlutou barvou
        buffers.draw(GL_TRIANGLES, shaderProgramLightPosition);

    }

    private void renderFromLight() {
        glUseProgram(shaderProgramLight);
        renderTarget.bind();

        glClearColor(0, 0.5f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locViewLight, false, cameraLight.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjectionLight, false, projection.floatArray());
        glUniform1f(locTimeLight, time);

        //
        if (showObject1) {
            glUniform1f(locTypeLight, 1f);
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }

        if (showObject2) {
            glUniform1f(locTypeLight, 2f);
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }

        if (showObject3) {
            glUniform1f(locTypeLight, 3f);
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }


        if (showObject4) {
            glUniform1f(locTypeLight, 4f);
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }

        if (showObject5) {
            glUniform1f(locTypeLight, 5f);
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }

        if (showObject6) {
            glUniform1f(locTypeLight, 6f);
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }

    }

    private void renderFromViewer() {
        glUseProgram(shaderProgramViewer);

        // nutno opravit viewport, protože render target si nastavuje vlastní
        glViewport(0, 0, width, height);

        // výchozí framebuffer - render do obrazovky
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glClearColor(0.5f, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());
        glUniform3fv(locLightPosition, ToFloatArray.convert(cameraLight.getPosition()));
        glUniformMatrix4fv(locLightVP, false, cameraLight.getViewMatrix().mul(projection).floatArray());
        glUniform1f(locTime, time);
        renderTarget.getDepthTexture().bind(shaderProgramViewer, "depthTexture", 1);
        textureMosaic.bind(shaderProgramViewer, "textureMosaic", 0);

        //
        if (showObject1) {
            glUniform1f(locType, 1f);
            buffers.draw(GL_TRIANGLES, shaderProgramViewer);
        }

        //
        if (showObject2) {
            glUniform1f(locType, 2f);
            buffers.draw(GL_TRIANGLES, shaderProgramViewer);
        }

        //
        if (showObject3) {
            glUniform1f(locType, 3f);
            buffers.draw(GL_TRIANGLES, shaderProgramViewer);
        }

        //
        if (showObject4) {
            glUniform1f(locType, 4f);
            buffers.draw(GL_TRIANGLES, shaderProgramViewer);
        }

        //
        if (showObject5) {
            glUniform1f(locType, 5f);
            buffers.draw(GL_TRIANGLES, shaderProgramViewer);
        }

        //
        if (showObject6) {
            glUniform1f(locType, 6f);
            buffers.draw(GL_TRIANGLES, shaderProgramViewer);
        }

        // create and draw text
        textRenderer.clear();
        textRenderer.addStr2D(width - 180, height - 3, "Jan Málek - PGRF 3 UHK");
        textRenderer.draw();
    }


    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cursorPosCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mouseButtonCallback;
    }

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    private double oldMx, oldMy;
    private boolean mousePressed;

    private GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mousePressed) {
                camera = camera.addAzimuth(Math.PI / 2 * (oldMx - x) / LwjglWindow.WIDTH);
                camera = camera.addZenith(Math.PI / 2 * (oldMy - y) / LwjglWindow.HEIGHT);
                oldMx = x;
                oldMy = y;
            }
        }
    };

    private GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                double[] xPos = new double[1];
                double[] yPos = new double[1];
                glfwGetCursorPos(window, xPos, yPos);
                oldMx = xPos[0];
                oldMy = yPos[0];
                mousePressed = action == GLFW_PRESS;
            }
        }
    };

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_D:
                        camera = camera.right(0.1);
                        break;
                    case GLFW_KEY_A:
                        camera = camera.left(0.1);
                        break;
                    case GLFW_KEY_W:
                        camera = camera.up(0.1);
                        break;
                    case GLFW_KEY_S:
                        camera = camera.down(0.1);
                        break;
                    case GLFW_KEY_Q:
                        camera = camera.forward(0.1);
                        break;
                    case GLFW_KEY_E:
                        camera = camera.backward(0.1);
                        break;
                    case GLFW_KEY_SPACE:
                        camera = camera.withFirstPerson(!camera.getFirstPerson());
                        break;
                }
            }
        }
    };

}
