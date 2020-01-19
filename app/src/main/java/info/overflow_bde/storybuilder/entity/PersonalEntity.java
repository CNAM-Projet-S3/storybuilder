package info.overflow_bde.storybuilder.entity;

import android.graphics.Bitmap;

public class PersonalEntity {
	public long id;
	public String title;
	public Bitmap image;

	public PersonalEntity(Long id,String title, Bitmap image) {
		this.id  = id;
		this.title = title;
		this.image = image;
	}
}
