/*
 * Copyright 2020 julian.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package laptopslideshow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * Program that displays a fullscreen slideshow from Pictures\LaptopSlideshow
 * @author julian
 */
public class LaptopSlideshow {
    
    // member variables
    static JFrame frame;
    static JLabel backgroundLabel;
    static JLabel textLabel;
    static ArrayList<BufferedImage> images;
    static int currentIndex;
    static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");  
    static SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
    
    // some code taken from here: 
    // https://stackoverflow.com/questions/11300847/load-and-display-all-the-images-from-a-folder/11301085
    
    // set directory path
    static String userHome = System.getProperty("user.home");
    static final File dir = new File(userHome+"\\Pictures\\LaptopSlideshow");
    
    // array of supported filetypes
    static final String[] EXTENSIONS = new String[] {
        "png", "jpg", "bmp", "gif"
    };
    
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    
    // main method
    public static void main(String[] args) {
        
        // set up Scanner object and get input from user
        Scanner myScanner = new Scanner(System.in);
        System.out.println("How many seconds should the program wait between frames?");
        int frameDelay = myScanner.nextInt();
        System.out.println("Type 1 if seconds should be displayed, type 0 if they shouldn't.");
        int secondInput = myScanner.nextInt();
        if (secondInput == 1) {
            timeFormat = new SimpleDateFormat("hh:mm:ss a");
        }
        
        // create new label for image background
        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout(64, 64));
        // create new label for date/time
        textLabel = new JLabel();
        textLabel.setFont(textLabel.getFont().deriveFont(Font.BOLD, 72));
        textLabel.setForeground(Color.WHITE);
        textLabel.setHorizontalAlignment(JLabel.LEFT);
        textLabel.setVerticalAlignment(JLabel.BOTTOM);
        Border border = textLabel.getBorder();
        Border margin = new EmptyBorder(32,32,32,32);
        textLabel.setBorder(new CompoundBorder(border, margin));
        
        // define Runnables for time-based tasks
        Runnable slideshowRunnable = new Runnable() {
            public void run() {
                advanceImage();
            }
        };
        
        Runnable updateTimeRunnable = new Runnable() {
            public void run() {
                updateTime();
            }
        };
        
        // schedule these Runnables to be executed at fixed rates
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(slideshowRunnable, frameDelay, frameDelay, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(updateTimeRunnable, 0, 1, TimeUnit.SECONDS);
        
        // set up window
        frame = new JFrame();
        
        frame.setSize(1920, 1080);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.getContentPane().add(backgroundLabel);
        backgroundLabel.add(textLabel);
        frame.pack();
        frame.setVisible(true);
        
        // store images in ArrayList
        images = new ArrayList<>();
        currentIndex = 0;
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(f);
                    images.add(img);
                } catch (final IOException e) {
                    // handle errors here
                }
            }
        }
        changeImage(images.get(0));
    } // close main method
    
    // method to change the displayed image
    public static void changeImage(BufferedImage img) {
        ImageIcon imageIcon = new ImageIcon(img); // not sure if this is necessary
        imageIcon.getImage().flush();
        backgroundLabel.setIcon(imageIcon);
        backgroundLabel.revalidate();
        backgroundLabel.repaint();
        updateTime();
    } // close method changeImage
    
    // method to display time on top of image
    public static void updateTime() {
        // format text according to current date/time
        Date date = new Date();
        textLabel.setText("<html>"+timeFormat.format(date)+"<br>"+
        dateFormat.format(date)+"</html>");
        frame.revalidate();
        frame.repaint();
    } // close method updateTime
    
    // method to advance the current image to the next one
    public static void advanceImage() {
        // get size of images list
        int imagesSize = images.size();
        // see if we can increment the current index
        if (currentIndex + 1 < imagesSize) {
            currentIndex++;
        } else { // if not, reset to first image
            currentIndex = 0;
        } // close if/else
        changeImage(images.get(currentIndex));
    } // close method advanceImage
    
} // close class LaptopSlideshow
