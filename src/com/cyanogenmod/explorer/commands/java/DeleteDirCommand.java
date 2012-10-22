/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.cyanogenmod.explorer.commands.java;

import android.util.Log;

import com.cyanogenmod.explorer.commands.DeleteDirExecutable;
import com.cyanogenmod.explorer.console.ExecutionException;
import com.cyanogenmod.explorer.console.InsufficientPermissionsException;
import com.cyanogenmod.explorer.console.NoSuchFileOrDirectory;
import com.cyanogenmod.explorer.model.MountPoint;
import com.cyanogenmod.explorer.util.MountPointHelper;

import java.io.File;


/**
 * A class for delete a folder.
 */
public class DeleteDirCommand extends Program implements DeleteDirExecutable {

    private static final String TAG = "DeleteDirCommand"; //$NON-NLS-1$

    private final String mPath;

    /**
     * Constructor of <code>DeleteDirCommand</code>.
     *
     * @param path The name of the new folder
     */
    public DeleteDirCommand(String path) {
        super();
        this.mPath = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getResult() {
        return Boolean.TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute()
            throws InsufficientPermissionsException, NoSuchFileOrDirectory, ExecutionException {
        if (isTrace()) {
            Log.v(TAG,
                    String.format("Deleting directory: %s", this.mPath)); //$NON-NLS-1$
        }

        File f = new File(this.mPath);
        if (!f.exists()) {
            if (isTrace()) {
                Log.v(TAG, "Result: FAIL. NoSuchFileOrDirectory"); //$NON-NLS-1$
            }
            throw new NoSuchFileOrDirectory(this.mPath);
        }

        // Check that if the path exist, it need to be a folder. Otherwise something is
        // wrong
        if (f.exists() && !f.isDirectory()) {
            if (isTrace()) {
                Log.v(TAG, "Result: FAIL. ExecutionException"); //$NON-NLS-1$
            }
            throw new ExecutionException("the path exists but is not a folder"); //$NON-NLS-1$
        }

        // Delete the file
        if (!deleteFolder(f)) {
            if (isTrace()) {
                Log.v(TAG, "Result: FAIL. InsufficientPermissionsException"); //$NON-NLS-1$
            }
            throw new InsufficientPermissionsException();
        }

        if (isTrace()) {
            Log.v(TAG, "Result: OK"); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MountPoint getWritableMountPoint() {
        return MountPointHelper.getMountPointFromDirectory(this.mPath);
    }

    /**
     * Method that deletes a folder recursively
     *
     * @param folder The folder to delete
     * @return boolean If the folder was deleted
     */
    private boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (!deleteFolder(files[i])) {
                        return false;
                    }
                } else {
                    if (!files[i].delete()) {
                        return false;
                    }
                }
            }
        }
        return folder.delete();
    }
}
