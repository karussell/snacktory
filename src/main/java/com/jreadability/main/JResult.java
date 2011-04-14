/*
 *  Copyright 2011 Peter Karich jetwick_@_pannous_._info
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jreadability.main;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class JResult {

    private String title;
    private String imageUrl;
    private String videoUrl;
    private String text;
    private String description;

    public String getDescription() {
        if (description == null)
            return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        if (imageUrl == null)
            return "";
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        // fall back to description which can be better determined
        if (text == null || text.isEmpty())
            return getDescription();

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        if (title == null)
            return "";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static boolean isVideoLink(String url) {
        if (url.startsWith("http://"))
            url = url.substring("http://".length());
        if (url.startsWith("www."))
            url = url.substring("www.".length());

        // strip mobile from start
        if (url.startsWith("m."))
            url = url.substring("m.".length());

        return url.startsWith("youtube.com") || url.startsWith("video.yahoo.com")
                || url.startsWith("vimeo.com") || url.startsWith("blip.tv");
    }

    @Override
    public String toString() {
        return "title:" + getTitle() + " imageUrl:" + getImageUrl() + " text:" + text;
    }
}
