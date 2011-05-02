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
package de.jetwick.snacktory;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class JResult {

    private String title;
    private String url;
    private String imageUrl;
    private String videoUrl;
    private String text;
    private String faviconUrl;
    private String description;

    public JResult() {
    }

    public String getUrl() {
        if (url == null)
            return "";
        return url;
    }

    public JResult setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getFaviconUrl() {
        if (faviconUrl == null)
            return "";
        return faviconUrl;
    }

    public JResult setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
        return this;
    }

    public String getDescription() {
        if (description == null)
            return "";
        return description;
    }

    public JResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImageUrl() {
        if (imageUrl == null)
            return "";
        return imageUrl;
    }

    public JResult setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getText() {
        // fall back to description which can be better determined
        if (text == null || text.isEmpty())
            return getDescription();

        return text;
    }

    public JResult setText(String text) {
        this.text = text;
        return this;
    }

    public String getTitle() {
        if (title == null)
            return "";
        return title;
    }

    public JResult setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getVideoUrl() {
        if (videoUrl == null)
            return "";
        return videoUrl;
    }

    public JResult setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    @Override
    public String toString() {
        return "title:" + getTitle() + " imageUrl:" + getImageUrl() + " text:" + text;
    }
}
